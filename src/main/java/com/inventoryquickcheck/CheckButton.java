package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.LinkBrowser;

/**
 * The small icon on the attack tab while the bank is open. A detached overlay: while RuneLite holds no saved place for
 * it, it puts itself on the tab each frame; once the player drags it (Alt+drag), RuneLite places it. It never saves a
 * place itself. Presses on it are taken by {@link ButtonClicks}, which reads the bounds this class publishes.
 * The picture is drawn in code: a red disc with a white "!" (the same design as the Hub tile).
 * Choice: the tab is the first of the three toplevel layouts whose attack tab is loaded and not hidden.
 */
public final class CheckButton extends Overlay
{
	static final int SIZE = 24;
	private static final int GAP = 4;
	/** The orange name after each of the button's right-click lines: the plugin's display name. */
	private static final String MENU_TARGET = "2h Inventory Setups Quick Check";

	private final Client client;
	private final InventoryQuickCheckConfig config;
	private final TooltipManager tooltipManager;
	private final BufferedImage image;
	private volatile Rectangle drawn;
	private volatile Rectangle anchor;
	/** The default spot last asked for; client thread only. */
	private Point lastPlace;

	/**
	 * The plugin is passed on to RuneLite so that the menu's Configure line opens this plugin's own settings page
	 * (RuneLite's config plugin does the opening, for any overlay that knows its plugin).
	 */
	public CheckButton(Plugin plugin, Client client, InventoryQuickCheckConfig config, TooltipManager tooltipManager)
	{
		super(plugin);
		this.client = client;
		this.config = config;
		this.tooltipManager = tooltipManager;
		this.image = makeImage();
		setPosition(OverlayPosition.DETACHED);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		// The user, 2026-11-05 (menu 1 on docs/pictures/menu_sheet.png): right-click shows Configure and Discord.
		addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE, MENU_TARGET);
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Discord", MENU_TARGET, e -> LinkBrowser.browse(SupportLinks.DISCORD_URL));
	}

	/** The bounds drawn on the last frame, or null when nothing is drawn. Read by the mouse listener. */
	Rectangle drawn()
	{
		return drawn;
	}

	/**
	 * Where the result box hangs from: the drawn bounds when the icon is drawn; else its default spot on the attack tab;
	 * else null. Worked out each frame on the client thread.
	 */
	Rectangle anchor()
	{
		return anchor;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!shouldDraw(BankState.isOpen(client), config.showButton()))
		{
			drawn = null;
			final Point tabSpot = spot(tabBounds(), null, null, SIZE, SIZE);
			anchor = tabSpot == null ? null : new Rectangle(tabSpot.x, tabSpot.y, SIZE, SIZE);
			return null;
		}
		// The renderer has already translated the graphics to this point (clamped to the screen).
		final Point at = getBounds().getLocation();
		if (getPreferredLocation() == null)
		{
			final Point place = spot(tabBounds(), itemsBounds(), null, SIZE, SIZE);
			if (place == null)
			{
				drawn = null;
				anchor = null;
				lastPlace = null;
				return null;
			}
			if (!place.equals(lastPlace))
			{
				// A new spot: move there and draw from the next frame, so the picture and the click area agree.
				lastPlace = place;
				getBounds().setLocation(place);
				drawn = null;
				anchor = new Rectangle(place.x, place.y, SIZE, SIZE);
				return new Dimension(SIZE, SIZE);
			}
		}
		else
		{
			// RuneLite places a dragged icon; after its reset the default spot must be set afresh.
			lastPlace = null;
		}
		graphics.drawImage(image, 0, 0, null);
		final Rectangle now = new Rectangle(at.x, at.y, SIZE, SIZE);
		drawn = now;
		anchor = now;
		final net.runelite.api.Point mouse = client.getMouseCanvasPosition();
		if (mouse != null && now.contains(mouse.getX(), mouse.getY()))
		{
			tooltipManager.add(new Tooltip("Check setup"));
		}
		return new Dimension(SIZE, SIZE);
	}

	/** Whether the icon is drawn at all. */
	static boolean shouldDraw(boolean bankOpen, boolean setting)
	{
		return bankOpen && setting;
	}

	/**
	 * Where the icon's top left goes: the saved point; else centred on the tab; else left of the bank-side items; else
	 * null (not drawn).
	 */
	static Point spot(Rectangle tab, Rectangle banksideItems, Point saved, int w, int h)
	{
		if (saved != null)
		{
			return saved;
		}
		if (tab != null)
		{
			return new Point(tab.x + (tab.width - w) / 2, tab.y + (tab.height - h) / 2);
		}
		if (banksideItems != null)
		{
			return new Point(banksideItems.x - GAP - w, banksideItems.y);
		}
		return null;
	}

	/**
	 * The attack tab of the layout in use. The game hides the tab's own widget while the bank is open (the stones seen
	 * then are the strip behind it), so its place is read whether hidden or not.
	 */
	private Rectangle tabBounds()
	{
		final int stone = stoneOf(client.getTopLevelInterfaceId());
		if (stone == -1)
		{
			return null;
		}
		final Widget widget = client.getWidget(stone);
		if (widget == null)
		{
			return null;
		}
		final Rectangle bounds = widget.getBounds();
		return bounds == null || bounds.width <= 0 || bounds.height <= 0 || bounds.x < 0 || bounds.y < 0 ? null : bounds;
	}

	/** The attack tab's widget for a toplevel interface, or -1 for one without tabs. */
	static int stoneOf(int toplevel)
	{
		if (toplevel == InterfaceID.TOPLEVEL)
		{
			return InterfaceID.Toplevel.STONE0;
		}
		if (toplevel == InterfaceID.TOPLEVEL_OSRS_STRETCH)
		{
			return InterfaceID.ToplevelOsrsStretch.STONE0;
		}
		if (toplevel == InterfaceID.TOPLEVEL_PRE_EOC)
		{
			return InterfaceID.ToplevelPreEoc.STONE0;
		}
		return -1;
	}

	private Rectangle itemsBounds()
	{
		final Widget widget = client.getWidget(InterfaceID.Bankside.ITEMS);
		return widget == null ? null : widget.getBounds();
	}

	private static BufferedImage makeImage()
	{
		final BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// The user's pick (picture 1 on docs/pictures/sheet.png, 2026-11-04): a red disc with a white "!".
		g.setColor(new Color(214, 48, 40));
		g.fillOval(2, 2, 20, 20);
		g.setColor(new Color(240, 240, 240));
		g.fillRect(11, 5, 2, 9);
		g.fillRect(11, 16, 2, 2);
		g.dispose();
		return img;
	}
}
