package com.inventoryquickcheck;

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
 */
public final class MarksOverlay extends WidgetItemOverlay
{
	private static final int MAX_CACHED = 64;

	private final Supplier<ShownResult> shown;
	private final InventoryQuickCheckConfig config;
	private final ItemManager itemManager;
	private final Map<String, BufferedImage> rings = new HashMap<>();

	public MarksOverlay(Supplier<ShownResult> shown, InventoryQuickCheckConfig config, ItemManager itemManager)
	{
		this.shown = shown;
		this.itemManager = itemManager;
		this.config = config;
		showOnInventory();
		showOnInterfaces(InterfaceID.BANKSIDE);
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		final ShownResult result = shown.get();
		if (result == null || !Blink.lit(System.currentTimeMillis() - result.startMs()))
		{
			return;
		}
		final Widget widget = widgetItem.getWidget();
		if (widget == null)
		{
			return;
		}
		final Integer expected = result.lowSlots().get(widget.getIndex());
		if (expected == null || expected != itemId)
		{
			return;
		}
		final Rectangle bounds = widgetItem.getCanvasBounds();
		final int thickness = config.markThickness();
		final int rgb = config.markColour().getRGB();
		final String key = itemId + ":" + thickness + ":" + rgb;
		BufferedImage ring = rings.get(key);
		if (ring == null)
		{
			final BufferedImage sprite = itemManager.getImage(itemId);
			if (sprite == null || !Ring.hasPixels(sprite))
			{
				return;
			}
			ring = Ring.of(sprite, thickness, config.markColour());
			if (rings.size() > MAX_CACHED)
			{
				rings.clear();
			}
			rings.put(key, ring);
		}
		graphics.drawImage(ring, bounds.x - thickness, bounds.y - thickness, null);
	}
}
