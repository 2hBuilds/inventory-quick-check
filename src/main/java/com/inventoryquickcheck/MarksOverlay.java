package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

/**
 * Rings (see {@link Ring}) the low potions on the inventory and on the bank-side inventory, blinking as {@link Blink}
 * says. The slot of a drawn item is the index of its own widget.
 * During the outline colour preview ({@link ColourPreview}) it also rings every potion carried, steady, in the
 * colour the supplier gives (the live picker's colour while it is open, else the saved one).
 */
public final class MarksOverlay extends WidgetItemOverlay
{
	private static final int MAX_CACHED = 64;

	private final Supplier<ShownResult> shown;
	private final Supplier<Map<Integer, Integer>> preview;
	private final Supplier<Color> colour;
	private final InventoryQuickCheckConfig config;
	private final ItemManager itemManager;
	private final Map<String, BufferedImage> rings = new HashMap<>();

	public MarksOverlay(Supplier<ShownResult> shown, Supplier<Map<Integer, Integer>> preview,
		Supplier<Color> colour, InventoryQuickCheckConfig config, ItemManager itemManager)
	{
		this.shown = shown;
		this.preview = preview;
		this.itemManager = itemManager;
		this.colour = colour;
		this.config = config;
		showOnInventory();
		showOnInterfaces(InterfaceID.BANKSIDE);
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		final Widget widget = widgetItem.getWidget();
		if (widget == null)
		{
			return;
		}
		final ShownResult result = shown.get();
		final Integer low = result == null
			|| !Blink.lit(System.currentTimeMillis() - result.startMs(), config.potionFlashes())
			? null : result.lowSlots().get(widget.getIndex());
		final Integer previewed = preview.get().get(widget.getIndex());
		if ((low == null || low != itemId) && (previewed == null || previewed != itemId))
		{
			return;
		}
		final Rectangle bounds = widgetItem.getCanvasBounds();
		final int thickness = config.markThickness();
		final Color markColour = colour.get();
		final int rgb = markColour.getRGB();
		final String key = itemId + ":" + thickness + ":" + rgb;
		BufferedImage ring = rings.get(key);
		if (ring == null)
		{
			final BufferedImage sprite = itemManager.getImage(itemId);
			if (sprite == null || !Ring.hasPixels(sprite))
			{
				return;
			}
			ring = Ring.of(sprite, thickness, markColour);
			if (rings.size() > MAX_CACHED)
			{
				rings.clear();
			}
			rings.put(key, ring);
		}
		graphics.drawImage(ring, bounds.x - thickness, bounds.y - thickness, null);
	}
}
