package com.inventoryquickcheck;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;
import net.runelite.client.input.MouseAdapter;

/**
 * Takes the left presses on the check button and runs the check. It never reads the client: the bounds are those of
 * the last drawn frame, because this runs on the AWT thread. A press with Alt held passes through, so RuneLite's own
 * Alt+drag and Alt+right-click still work on the icon; a right press passes through too, so the game's menu opens
 * with the button's own lines (Configure, Discord).
 * Choice: the click that follows a consumed press is consumed too, and so is the release.
 */
public final class ButtonClicks extends MouseAdapter
{
	private final Supplier<Rectangle> drawn;
	private final Runnable check;
	private boolean pressTaken;
	private boolean clickDue;

	public ButtonClicks(Supplier<Rectangle> drawn, Runnable check)
	{
		this.drawn = drawn;
		this.check = check;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent e)
	{
		clickDue = false;
		final Rectangle bounds = drawn.get();
		if (bounds != null && bounds.contains(e.getPoint()) && !e.isAltDown() && e.getButton() == MouseEvent.BUTTON1)
		{
			e.consume();
			pressTaken = true;
			check.run();
		}
		return e;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent e)
	{
		if (pressTaken)
		{
			e.consume();
			pressTaken = false;
			clickDue = true;
		}
		return e;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent e)
	{
		if (clickDue)
		{
			e.consume();
			clickDue = false;
		}
		return e;
	}
}
