package com.inventoryquickcheck;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Supplier;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;

/**
 * The one-line answers - All good and the two setup messages in {@link Texts} - in a small label just
 * above the check button's icon, its right edge level with the icon's, wherever the icon is (the user, 2026-11-05:
 * "show that text always just above where the current placement of the button is? even if we drag?"). It follows the
 * icon and cannot be dragged on its own. The list of pictures stays {@link ResultOverlay}'s.
 */
public final class MessageOverlay extends Overlay
{
	private static final int PADDING = 4;

	private final Supplier<ShownResult> shown;
	private final InventoryQuickCheckConfig config;
	private final Supplier<Rectangle> anchor;
	private final Client client;
	/** The place last asked for; render thread only. */
	private Point lastPlace;

	public MessageOverlay(Supplier<ShownResult> shown, InventoryQuickCheckConfig config, Supplier<Rectangle> anchor,
		Client client)
	{
		this.shown = shown;
		this.config = config;
		this.anchor = anchor;
		this.client = client;
		setPosition(OverlayPosition.DETACHED);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setMovable(false);
		setResettable(false);
		setSnappable(false);
	}

	@Override
	public String getName()
	{
		return "InventoryQuickCheckMessage";
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final ShownResult result = shown.get();
		if (result == null || result.message() == null || !result.rows().isEmpty()
			|| ResultOverlay.over(result, System.currentTimeMillis(), config.secondsToShow(), BankState.isOpen(client))
			|| !ResultOverlay.showsBox(BankState.isOpen(client), config.hotkeyBoxWhenBankClosed()))
		{
			return null;
		}
		final FontMetrics metrics = graphics.getFontMetrics();
		final Dimension size = new Dimension(metrics.stringWidth(result.message()) + 2 * PADDING,
			metrics.getAscent() + metrics.getDescent() + 2 * PADDING);
		final Point place = BoxPlace.above(anchor.get(), size,
			new Dimension(client.getCanvasWidth(), client.getCanvasHeight()));
		if (!place.equals(lastPlace))
		{
			// A new place (the icon moved, or the text changed): move there and draw from the next frame.
			lastPlace = place;
			getBounds().setLocation(place);
			return size;
		}
		graphics.setColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, size.width, size.height);
		ResultOverlay.text(graphics, result.message(), result.messageColour(), PADDING, PADDING + metrics.getAscent());
		return size;
	}
}
