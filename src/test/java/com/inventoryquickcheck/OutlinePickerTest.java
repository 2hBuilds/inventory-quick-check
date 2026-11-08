package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;

/** F4: where the outline colour picker opens. */
public class OutlinePickerTest
{
	private static final Rectangle SCREEN = new Rectangle(0, 0, 1920, 1080);
	private static final Dimension PICKER = new Dimension(300, 400);

	@Test
	public void f4_tenPixelsInFromTheCanvasCorner()
	{
		assertEquals(new Point(110, 60), OutlinePicker.spot(new Rectangle(100, 50, 800, 600), PICKER, SCREEN));
	}

	@Test
	public void f4_keptInsideTheScreenAtTheFarEdges()
	{
		assertEquals(new Point(1620, 680), OutlinePicker.spot(new Rectangle(1700, 900, 800, 600), PICKER, SCREEN));
	}

	@Test
	public void f4_keptInsideTheScreenAtTheNearEdges()
	{
		assertEquals(new Point(0, 0), OutlinePicker.spot(new Rectangle(-50, -20, 800, 600), PICKER, SCREEN));
	}
}
