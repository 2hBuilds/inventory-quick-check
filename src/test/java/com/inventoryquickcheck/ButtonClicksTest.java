package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Canvas;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/** T22: which presses the button takes. */
public class ButtonClicksTest
{
	private final Canvas canvas = new Canvas();
	private final AtomicInteger checks = new AtomicInteger();

	private ButtonClicks clicks(Rectangle drawn)
	{
		return new ButtonClicks(() -> drawn, checks::incrementAndGet);
	}

	private MouseEvent event(int id, int x, int y, int modifiers)
	{
		return new MouseEvent(canvas, id, 0L, modifiers, x, y, 1, false, MouseEvent.BUTTON1);
	}

	@Test
	public void pressInsideIsConsumedAndRunsTheCheck()
	{
		final MouseEvent e = event(MouseEvent.MOUSE_PRESSED, 20, 20, 0);
		clicks(new Rectangle(10, 10, 24, 24)).mousePressed(e);
		assertTrue(e.isConsumed());
		assertEquals(1, checks.get());
	}

	@Test
	public void pressWithAltPassesThrough()
	{
		final MouseEvent e = event(MouseEvent.MOUSE_PRESSED, 20, 20, InputEvent.ALT_DOWN_MASK);
		clicks(new Rectangle(10, 10, 24, 24)).mousePressed(e);
		assertFalse(e.isConsumed());
		assertEquals(0, checks.get());
	}

	@Test
	public void rightPressPassesThroughForTheMenu()
	{
		// The user, 2026-11-05 (menu 1): right-click opens the menu with Configure and Discord; only left click checks.
		final MouseEvent e = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, 0L, 0, 20, 20, 1, true,
			MouseEvent.BUTTON3);
		clicks(new Rectangle(10, 10, 24, 24)).mousePressed(e);
		assertFalse(e.isConsumed());
		assertEquals(0, checks.get());
	}

	@Test
	public void pressOutsidePassesThrough()
	{
		final MouseEvent e = event(MouseEvent.MOUSE_PRESSED, 100, 100, 0);
		clicks(new Rectangle(10, 10, 24, 24)).mousePressed(e);
		assertFalse(e.isConsumed());
		assertEquals(0, checks.get());
	}

	@Test
	public void nothingDrawnPassesThrough()
	{
		final MouseEvent e = event(MouseEvent.MOUSE_PRESSED, 20, 20, 0);
		clicks(null).mousePressed(e);
		assertFalse(e.isConsumed());
		assertEquals(0, checks.get());
	}

	@Test
	public void releaseAfterATakenPressIsConsumed()
	{
		final ButtonClicks c = clicks(new Rectangle(10, 10, 24, 24));
		c.mousePressed(event(MouseEvent.MOUSE_PRESSED, 20, 20, 0));
		final MouseEvent release = event(MouseEvent.MOUSE_RELEASED, 20, 20, 0);
		c.mouseReleased(release);
		assertTrue(release.isConsumed());
		final MouseEvent click = event(MouseEvent.MOUSE_CLICKED, 20, 20, 0);
		c.mouseClicked(click);
		assertTrue(click.isConsumed());
	}

	@Test
	public void releaseAfterAnUntakenPressPassesThrough()
	{
		final ButtonClicks c = clicks(new Rectangle(10, 10, 24, 24));
		c.mousePressed(event(MouseEvent.MOUSE_PRESSED, 100, 100, 0));
		final MouseEvent release = event(MouseEvent.MOUSE_RELEASED, 100, 100, 0);
		c.mouseReleased(release);
		assertFalse(release.isConsumed());
	}
}
