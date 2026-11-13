package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;

/**
 * The box with the answer: a grid of item pictures with {@code x<k>} beside each, wrapped by the Box layout setting.
 * Its default place is the Box place setting's (the spot the button works out for the choice); the player may
 * Alt+drag it elsewhere (RuneLite then places it, and the plugin switches the setting to Where I drag it) and
 * Alt+right-click it back. Hidden draws no box and locks it (not movable or resettable); it is never snappable.
 * Its right edge holds still as it shrinks (the user,
 * 2026-11-05: "take the rightmost column location when we collapse it"): for one check the overlay keeps the width of
 * its widest moment and draws the box against that width's right edge, the space to its left left clear - so it
 * works wherever RuneLite pins the overlay's top-left corner. It never saves a place itself.
 * A check from the hotkey hides itself, and clears the plugin's result, when the seconds to show are up; a check from
 * the button stays until the plugin clears it (the bank closing).
 */
public final class ResultOverlay extends Overlay
{
	private static final int PADDING = 4;
	private static final int PICTURE_WIDTH = 36;
	private static final int PICTURE_HEIGHT = 32;
	private static final int ROW_HEIGHT = 36;
	private static final Color TITLE_COLOUR = new Color(255, 152, 31);
	private static final int TITLE_PADDING = 8;
	private static final int GAP = 4;
	private static final int TEXT_WIDTH = 26;
	private static final int COLUMN_WIDTH = PICTURE_WIDTH + GAP + TEXT_WIDTH;

	private final Supplier<ShownResult> shown;
	private final Runnable clear;
	private final InventoryQuickCheckConfig config;
	private final Function<MissingBoxPlace, BoxAnchor> boxSpot;
	private final Client client;
	/** The default place last asked for; render thread only. */
	private Point lastPlace;
	/** The widest the box has been for the check that started at {@link #widestFor}; render thread only. */
	private int widest;
	private long widestFor = Long.MIN_VALUE;

	public ResultOverlay(Supplier<ShownResult> shown, Runnable clear, InventoryQuickCheckConfig config,
		Function<MissingBoxPlace, BoxAnchor> boxSpot, Client client)
	{
		this.shown = shown;
		this.clear = clear;
		this.config = config;
		this.boxSpot = boxSpot;
		this.client = client;
		setPosition(OverlayPosition.DETACHED);
		apply(config.boxPlace());
	}

	/** Whether the box may be dragged: every choice but Hidden. */
	static boolean movable(MissingBoxPlace place)
	{
		return place != MissingBoxPlace.HIDDEN;
	}

	/** Locks or frees the box for a Box place choice. */
	void apply(MissingBoxPlace place)
	{
		final boolean free = movable(place);
		setMovable(free);
		setSnappable(false);
		setResettable(free);
	}

	/**
	 * A name of its own, so a place RuneLite saved for the box is never loaded from another overlay.
	 */
	@Override
	public String getName()
	{
		return "InventoryQuickCheckBox";
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final ShownResult result = shown.get();
		if (result == null)
		{
			return null;
		}
		if (over(result, System.currentTimeMillis(), config.secondsToShow(), BankState.isOpen(client)))
		{
			// The bank has closed (go this frame, not on the next game tick, which stays as the backstop), or a hotkey
			// check's time is up.
			clear.run();
			return null;
		}
		if (!showsBox(BankState.isOpen(client), config.hotkeyBoxWhenBankClosed()))
		{
			return null;
		}
		// The one-line messages (All good, the setup messages) are MessageOverlay's, above the icon.
		final List<ShownResult.Row> rows = result.rows();
		if (rows.isEmpty() || config.boxPlace() == MissingBoxPlace.HIDDEN)
		{
			return null;
		}
		final Dimension size = gridSize(graphics.getFontMetrics(), rows.size(), config.boxLayout());
		if (widestFor != result.startMs())
		{
			widestFor = result.startMs();
			widest = 0;
		}
		widest = Math.max(widest, size.width);
		final Dimension frame = new Dimension(widest, size.height);
		if (getPreferredLocation() == null)
		{
			final Dimension canvas = new Dimension(client.getCanvasWidth(), client.getCanvasHeight());
			final BoxAnchor hang = boxSpot.apply(config.boxPlace());
			// Choice: no spot (null) is the canvas corner, as before.
			final Point place = hang == null ? BoxPlace.of(null, frame, canvas) : hang.place(frame, canvas);
			if (!place.equals(lastPlace))
			{
				// A new place (the icon moved, or the box grew): move there and draw from the next frame, so the box
				// lands where it is placed.
				lastPlace = place;
				getBounds().setLocation(place);
				return frame;
			}
		}
		else
		{
			// RuneLite places a dragged box; after its reset the default place must be set afresh.
			lastPlace = null;
		}
		// The box against the frame's right edge; the space to its left stays clear.
		final int shift = frame.width - size.width;
		graphics.translate(shift, 0);
		drawGrid(graphics, rows, config.boxLayout());
		graphics.translate(-shift, 0);
		return frame;
	}

	/**
	 * Whether a shown result is over: one that started at the bank (button or hotkey) when the bank closes, and not
	 * before (the user, 2026-11-12: a hotkey check at the bank works like the button); one that started away from the
	 * bank when its seconds are up.
	 */
	static boolean over(ShownResult result, long nowMs, int secondsToShow, boolean bankOpen)
	{
		if (result.startedAtBank())
		{
			return !bankOpen;
		}
		return nowMs - result.startMs() >= secondsToShow * 1000L;
	}

	/**
	 * Whether the box and its one-line messages show: always with the bank open; with it closed (a hotkey check) only
	 * when the setting asks, else the potion outlines are the whole answer (the user, 2026-11-12: "maybe the user just
	 * wants to drink a non full potion").
	 */
	static boolean showsBox(boolean bankOpen, boolean whenBankClosed)
	{
		return bankOpen || whenBankClosed;
	}

	private static int titleHeight(FontMetrics metrics)
	{
		return metrics.getHeight() + 4;
	}

	private static Dimension gridSize(FontMetrics metrics, int rowCount, BoxLayout layout)
	{
		final BoxGrid grid = BoxGrid.of(rowCount, layout);
		final int width = Math.max(grid.columns() * COLUMN_WIDTH + 2 * PADDING,
			metrics.stringWidth(Texts.MISSING_TITLE) + TITLE_PADDING);
		return new Dimension(width, grid.rows() * ROW_HEIGHT + 2 * PADDING + titleHeight(metrics));
	}

	private static Dimension drawGrid(Graphics2D graphics, List<ShownResult.Row> rows, BoxLayout layout)
	{
		final FontMetrics metrics = graphics.getFontMetrics();
		final BoxGrid grid = BoxGrid.of(rows.size(), layout);
		final int perColumn = layout.rowsPerColumn();
		final Dimension size = gridSize(metrics, rows.size(), layout);
		final int top = titleHeight(metrics);
		graphics.setColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, size.width, size.height);
		text(graphics, Texts.MISSING_TITLE, TITLE_COLOUR, (size.width - metrics.stringWidth(Texts.MISSING_TITLE)) / 2,
			2 + metrics.getAscent());
		for (int i = 0; i < grid.shown(); i++)
		{
			final ShownResult.Row row = rows.get(i);
			final Point cell = cellOrigin(i, perColumn, top);
			if (row.image != null)
			{
				graphics.drawImage(row.image, cell.x, cell.y + (ROW_HEIGHT - PICTURE_HEIGHT) / 2, PICTURE_WIDTH,
					PICTURE_HEIGHT, null);
			}
			text(graphics, "x" + row.count, Color.WHITE, cell.x + PICTURE_WIDTH + GAP, baseline(cell.y, metrics));
		}
		if (grid.more() > 0)
		{
			final Point cell = cellOrigin(grid.shown(), perColumn, top);
			text(graphics, "+" + grid.more(), Color.YELLOW, cell.x, baseline(cell.y, metrics));
		}
		return size;
	}

	private static Point cellOrigin(int index, int perColumn, int top)
	{
		final Point place = BoxGrid.place(index, perColumn);
		return new Point(PADDING + place.x * COLUMN_WIDTH, top + PADDING + place.y * ROW_HEIGHT);
	}

	private static int baseline(int rowTop, FontMetrics metrics)
	{
		return rowTop + (ROW_HEIGHT - (metrics.getAscent() + metrics.getDescent())) / 2 + metrics.getAscent();
	}

	/** Text with a 1 px black shadow, as the box and the message label draw it. */
	static void text(Graphics2D graphics, String text, Color colour, int x, int baseline)
	{
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, baseline + 1);
		graphics.setColor(colour);
		graphics.drawString(text, x, baseline);
	}
}
