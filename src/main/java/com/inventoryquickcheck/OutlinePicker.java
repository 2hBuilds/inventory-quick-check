package com.inventoryquickcheck;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

/**
 * Opens RuneLite's own colour picker (alpha hidden) for the outline colour, so the potions can be recoloured live: it
 * reports every move of the reader's hand and tells when it closes. It opens at the game canvas's top left plus 10 px,
 * kept on the screen, so it does not cover the inventory.
 * Choice: with no manager, no canvas or no picker, nothing opens.
 */
final class OutlinePicker
{
	private static final int OFFSET = 10;

	private final ColorPickerManager manager;
	private final Client client;

	OutlinePicker(ColorPickerManager manager, Client client)
	{
		this.manager = manager;
		this.client = client;
	}

	/** Opens the picker on the Swing thread; {@code live} hears every move, {@code done} hears the colour at close. */
	void open(Color start, Consumer<Color> live, Consumer<Color> done)
	{
		SwingUtilities.invokeLater(() ->
		{
			final Canvas canvas = client.getCanvas();
			if (manager == null || canvas == null)
			{
				return;
			}
			final RuneliteColorPicker picker = manager.create(client, start, "Outline colour", true);
			if (picker == null)
			{
				return;
			}
			picker.setOnColorChange(live);
			picker.setOnClose(done);
			final GraphicsConfiguration screen = canvas.getGraphicsConfiguration();
			if (canvas.isShowing() && screen != null)
			{
				picker.setLocation(spot(new Rectangle(canvas.getLocationOnScreen(), canvas.getSize()), picker.getSize(),
					screen.getBounds()));
			}
			picker.setVisible(true);
		});
	}

	/** The picker's top left: the canvas's top left plus 10 px, each coordinate clamped to keep it on the screen. */
	static Point spot(Rectangle canvasOnScreen, Dimension picker, Rectangle screen)
	{
		final int x = Math.max(screen.x, Math.min(canvasOnScreen.x + OFFSET, screen.x + screen.width - picker.width));
		final int y = Math.max(screen.y, Math.min(canvasOnScreen.y + OFFSET, screen.y + screen.height - picker.height));
		return new Point(x, y);
	}
}
