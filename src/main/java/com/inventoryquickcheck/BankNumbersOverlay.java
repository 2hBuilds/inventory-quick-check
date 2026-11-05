package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.function.Supplier;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

/**
 * Draws on each bank item that answers an entry of the box how many to take out, in bold orange with a black edge at its
 * bottom-right corner. The bank's own amount at the top-left is left alone.
 */
public final class BankNumbersOverlay extends WidgetItemOverlay
{
	private static final Color ORANGE = new Color(255, 152, 31);

	private final Supplier<ShownResult> shown;
	private final InventoryQuickCheckConfig config;

	public BankNumbersOverlay(Supplier<ShownResult> shown, InventoryQuickCheckConfig config)
	{
		this.shown = shown;
		this.config = config;
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (!config.numberBankItems())
		{
			return;
		}
		final ShownResult result = shown.get();
		if (result == null)
		{
			return;
		}
		final Integer number = result.withdraw().get(itemId);
		if (number == null)
		{
			return;
		}
		final Rectangle bounds = widgetItem.getCanvasBounds();
		final Font font = FontManager.getRunescapeBoldFont();
		graphics.setFont(font);
		final FontMetrics metrics = graphics.getFontMetrics(font);
		final String text = String.valueOf(number);
		final int x = bounds.x + bounds.width - 1 - metrics.stringWidth(text);
		final int y = bounds.y + bounds.height - 1;
		graphics.setColor(Color.BLACK);
		for (int dy = -1; dy <= 1; dy++)
		{
			for (int dx = -1; dx <= 1; dx++)
			{
				if (dx != 0 || dy != 0)
				{
					graphics.drawString(text, x + dx, y + dy);
				}
			}
		}
		graphics.setColor(ORANGE);
		graphics.drawString(text, x, y);
	}
}
