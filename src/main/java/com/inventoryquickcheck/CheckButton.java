package com.inventoryquickcheck;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;
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
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

/**
 * The small icon shown while the bank is open. A detached overlay locked to its place (not movable, snappable or
 * resettable): each frame it puts itself where "Button place" says (above the inventory, inside the first slot, or on
 * the attack tab), read from the bank-side inventory's slots. A spot an older build saved is dropped by RuneLite
 * when the overlay is added. It never saves a place itself. Presses on it are taken by {@link ButtonClicks}, which
 * reads the bounds this class publishes.
 * The picture is the chosen "Button look"'s: its 24 px picture at 24, its 16 px picture at 16, else the 24 px one
 * scaled. The pictures are loaded when the look changes and kept.
 * Choice: the tab is the first of the three toplevel layouts whose attack tab is loaded and not hidden.
 * Choice: a place whose widgets cannot be read falls back to the attack tab's spot.
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
	private final Supplier<ShownResult> shown;
	/** The look the pictures below were loaded for; client thread only. */
	private ButtonLook loadedLook;
	/** The look's 24 px picture; client thread only. */
	private BufferedImage image;
	/** The look's 16 px picture; client thread only. */
	private BufferedImage smallPicture;
	/** The 24 px picture scaled to an odd size, at the size last asked for; client thread only. */
	private BufferedImage smallImage;
	private volatile Rectangle drawn;
	private volatile Rectangle anchor;
	/** The default spot last asked for; client thread only. */
	private Point lastPlace;
	/** The icon size last drawn; client thread only. */
	private int lastSize;
	/** The attack tab's spot for the box (as {@link #anchor} when no slot is readable); written each frame. */
	private volatile Rectangle attackSpot;
	/** The bank-side inventory's first slot and the one beside it, while the bank is open; written each frame. */
	private volatile Rectangle slot0Now;
	private volatile Rectangle slot1Now;
	/** The bank's outer frame while the bank is open; written each frame. */
	private volatile Rectangle bankFrame;
	/** Whether the resizable modern layout is up; written each frame. */
	private volatile boolean modern;
	/** Whether the fixed layout is up; written each frame. */
	private volatile boolean fixedLayout;
	/** The attack tab's centre as an offset from the first slot, read once per bank opening; client thread only. */
	private Point attackOffset;
	/** The toplevel {@link #attackOffset} was read for; client thread only. */
	private int attackOffsetFor;
	/** The skills tab's centre as an offset from the first slot, read once per bank opening; client thread only. */
	private Point skillsOffset;
	/** The toplevel {@link #skillsOffset} was read for; client thread only. */
	private int skillsOffsetFor;

	/**
	 * The plugin is passed on to RuneLite so that the menu's Configure line opens this plugin's own settings page
	 * (RuneLite's config plugin does the opening, for any overlay that knows its plugin).
	 */
	public CheckButton(Plugin plugin, Client client, InventoryQuickCheckConfig config, TooltipManager tooltipManager,
		Supplier<ShownResult> shown, Runnable outlineColour)
	{
		super(plugin);
		this.client = client;
		this.config = config;
		this.tooltipManager = tooltipManager;
		this.shown = shown;
		setPosition(OverlayPosition.DETACHED);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		// The user, 2026-11-08: it once sat "under another layer of something on the screen and somewhere i didnt
		// expect it"; so it stays on the tab and draws above the other detached overlays.
		setMovable(false);
		setSnappable(false);
		setResettable(false);
		setPriority(PRIORITY_HIGHEST);
		// The user, 2026-11-05 (menu 1 on docs/pictures/menu_sheet.png): right-click shows Configure, Outline colour,
		// Discord.
		addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OverlayManager.OPTION_CONFIGURE, MENU_TARGET);
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Outline colour", MENU_TARGET, e ->
		{
			if (outlineColour != null)
			{
				outlineColour.run();
			}
		});
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

	/**
	 * Where the result box hangs from for a Box place choice, worked out from the values read on the last frame (on
	 * the client thread): Right of the bank is just right of the bank's frame; Left of the inventory is left of the
	 * attack tab's spot (left of the side panel), or on the modern layout two slot columns left of the first slot.
	 * Automatic and Where I drag it are Right of the bank, but Left of the inventory on the fixed layout. A spot whose widgets cannot be read, and the bank closed, fall
	 * back to the attack tab's spot.
	 * Choice: Hidden is never drawn, so it gets the attack tab's spot too.
	 */
	BoxAnchor boxSpot(MissingBoxPlace place)
	{
		final MissingBoxPlace rule = MissingBoxPlace.forLayout(place, fixedLayout);
		final Rectangle bank = bankFrame;
		if (rule == MissingBoxPlace.RIGHT_OF_BANK && bank != null)
		{
			return BoxAnchor.rightOf(bank);
		}
		final Rectangle slot0 = slot0Now;
		if (rule == MissingBoxPlace.LEFT_OF_INVENTORY && modern && slot0 != null)
		{
			final int pitch = slot0.width + ButtonSpot.gap(slot0, slot1Now);
			return BoxAnchor.beside(ButtonSpot.leftOfModernInventory(slot0, pitch));
		}
		return BoxAnchor.beside(attackSpot);
	}

	/** Reads and keeps what {@link #boxSpot} needs for this frame. */
	private void publish(Rectangle attack, boolean bankOpen)
	{
		attackSpot = attack;
		final int toplevel = client.getTopLevelInterfaceId();
		modern = toplevel == InterfaceID.TOPLEVEL_PRE_EOC;
		fixedLayout = toplevel == InterfaceID.TOPLEVEL;
		slot0Now = bankOpen ? slotBounds(0) : null;
		slot1Now = bankOpen ? slotBounds(1) : null;
		final Widget frame = bankOpen ? client.getWidget(InterfaceID.Bankmain.FRAME) : null;
		final Rectangle bounds = frame == null || frame.isHidden() ? null : frame.getBounds();
		bankFrame = bounds == null || bounds.width <= 0 || bounds.height <= 0 ? null : bounds;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!shouldDraw(BankState.isOpen(client), config.showButton()))
		{
			drawn = null;
			skillsOffset = null;
			attackOffset = null;
			final Point tabSpot = spot(tabBounds(), null, null, SIZE, SIZE);
			anchor = tabSpot == null ? null : new Rectangle(tabSpot.x, tabSpot.y, SIZE, SIZE);
			publish(anchor, false);
			return null;
		}
		// The renderer has already translated the graphics to this point (clamped to the screen).
		final Point at = getBounds().getLocation();
		final Point tabPlace = spot(tabBounds(), itemsBounds(), null, SIZE, SIZE);
		final Rectangle slotIcon = slotSpot(config.buttonPlace());
		final int size = slotIcon == null ? SIZE : slotIcon.width;
		final Point place = slotIcon == null ? tabPlace : slotIcon.getLocation();
		if (place == null)
		{
			drawn = null;
			anchor = null;
			publish(attackSpotOf(tabPlace, null), true);
			lastPlace = null;
			return null;
		}
		if (!place.equals(lastPlace) || size != lastSize)
		{
			// A new spot or size: move there and draw from the next frame, so the picture and the click area agree.
			lastPlace = place;
			lastSize = size;
			getBounds().setLocation(place);
			drawn = null;
			anchor = new Rectangle(place.x, place.y, size, size);
			publish(attackSpotOf(tabPlace, anchor), true);
			return new Dimension(size, size);
		}
		loadLook(config.buttonLook());
		graphics.drawImage(pictureFor(size), 0, 0, null);
		final Rectangle now = new Rectangle(at.x, at.y, size, size);
		drawn = now;
		anchor = now;
		publish(attackSpotOf(tabPlace, now), true);
		final net.runelite.api.Point mouse = client.getMouseCanvasPosition();
		if (mouse != null && now.contains(mouse.getX(), mouse.getY()))
		{
			final ShownResult result = shown.get();
			tooltipManager.add(new Tooltip(tooltip(result == null ? null : result.hint())));
		}
		return new Dimension(size, size);
	}

	/**
	 * The attack-tab spot for the box while the bank is open: the live first slot plus the attack tab's offset (read once
	 * per opening); else the live attack-tab spot; else the button's own bounds.
	 */
	private Rectangle attackSpotOf(Point tabPlace, Rectangle own)
	{
		final int toplevel = client.getTopLevelInterfaceId();
		final Rectangle slot0 = slotBounds(0);
		if (slot0 != null && (attackOffset == null || attackOffsetFor != toplevel))
		{
			// The hidden tab's bounds go stale on a resize, so its centre is read once per opening.
			attackOffset = ButtonSpot.tabOffset(tabBounds(), slot0);
			attackOffsetFor = toplevel;
		}
		final Rectangle fromSlot = ButtonSpot.tabSpot(slot0, attackOffset, SIZE);
		if (fromSlot != null)
		{
			return fromSlot;
		}
		return tabPlace == null ? own : new Rectangle(tabPlace.x, tabPlace.y, SIZE, SIZE);
	}

	/**
	 * The icon's bounds for the chosen place, from the bank-side inventory's first slot and its neighbours (the one
	 * beside it for the gap, the one below it for the row pitch); null for the attack tab or when the slots cannot be
	 * read.
	 */
	private Rectangle slotSpot(ButtonPlace choice)
	{
		if (choice == ButtonPlace.ATTACK_TAB)
		{
			return null;
		}
		final Rectangle slot0 = slotBounds(0);
		if (slot0 == null)
		{
			return null;
		}
		if (choice == ButtonPlace.INSIDE_FIRST_SLOT)
		{
			final int small = ButtonSpot.smallSize(slot0);
			final Point at = ButtonSpot.insideFirstSlot(slot0, ButtonSpot.gap(slot0, slotBounds(1)), small);
			return new Rectangle(at.x, at.y, small, small);
		}
		final int toplevel = client.getTopLevelInterfaceId();
		final int stone = ButtonSpot.skillsStoneOf(toplevel);
		if (stone != -1 && (skillsOffset == null || skillsOffsetFor != toplevel))
		{
			// The hidden tab's bounds go stale on a resize, so its centre is read once per opening.
			skillsOffset = ButtonSpot.tabOffset(stoneBounds(stone), slot0);
			skillsOffsetFor = toplevel;
		}
		final Point at = ButtonSpot.aboveInventory(slot0, ButtonSpot.pitch(slot0, slotBounds(4)),
			stone == -1 ? null : skillsOffset, SIZE);
		return new Rectangle(at.x, at.y, SIZE, SIZE);
	}

	/** Loads the chosen look's pictures when the look has changed; a change shows on the next frame. */
	private void loadLook(ButtonLook look)
	{
		if (look == loadedLook)
		{
			return;
		}
		loadedLook = look;
		image = ImageUtil.loadImageResource(CheckButton.class, look.picture());
		smallPicture = ImageUtil.loadImageResource(CheckButton.class, look.smallPicture());
		smallImage = null;
	}

	/** The picture for a size: the 24 px one, the 16 px one, or the 24 px one scaled (kept until the size changes). */
	private BufferedImage pictureFor(int size)
	{
		if (size == SIZE)
		{
			return image;
		}
		if (size == smallPicture.getWidth())
		{
			return smallPicture;
		}
		if (smallImage == null || smallImage.getWidth() != size)
		{
			smallImage = ImageUtil.resizeImage(image, size, size);
		}
		return smallImage;
	}

	/** A slot of the bank-side inventory, or null when missing or empty-sized. */
	private Rectangle slotBounds(int index)
	{
		final Widget items = client.getWidget(InterfaceID.Bankside.ITEMS);
		final Widget slot = items == null ? null : items.getChild(index);
		final Rectangle bounds = slot == null ? null : slot.getBounds();
		return bounds == null || bounds.width <= 0 || bounds.height <= 0 ? null : bounds;
	}

	/** The hover text: Check setup, then the hint's rows when a no-setup result is up. */
	static String tooltip(String hint)
	{
		return hint == null ? "Check setup" : "Check setup</br>" + hint;
	}

	/** Whether the icon is drawn at all. */
	static boolean shouldDraw(boolean bankOpen, boolean setting)
	{
		return bankOpen && setting;
	}

	/**
	 * Where the icon's top left goes: the saved point; else centred on the tab; else left of the bank-side items; else
	 * null (not drawn). This is the attack-tab spot: the Button place choice "Attack tab", the fallback of the other
	 * places, and the anchor while the bank is closed.
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
		return stoneBounds(stoneOf(client.getTopLevelInterfaceId()));
	}

	/** A tab's bounds, read whether hidden or not; null for -1, a missing widget or an off-screen or empty bound. */
	private Rectangle stoneBounds(int stone)
	{
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
}
