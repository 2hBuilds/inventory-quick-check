package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;

public class BoxAnchorTest
{
	private static final Dimension BOX = new Dimension(80, 200);
	private static final Dimension CANVAS = new Dimension(1000, 800);

	@Test
	public void m4a_besideHangsLeftTopsLevel()
	{
		final Rectangle rect = new Rectangle(500, 300, 24, 24);
		assertEquals(BoxPlace.of(rect, BOX, CANVAS), BoxAnchor.beside(rect).place(BOX, CANVAS));
	}

	@Test
	public void n4_rightOfTheBankAQuarterDown()
	{
		assertEquals(new Point(592, 190), BoxAnchor.rightOf(new Rectangle(100, 40, 488, 600))
			.place(new Dimension(80, 300), new Dimension(1600, 900)));
	}
}
