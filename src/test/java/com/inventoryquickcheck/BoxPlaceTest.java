package com.inventoryquickcheck;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BoxPlaceTest
{
	@Test
	public void t26a_sitsLeftOfTheIconTopsLevel()
	{
		assertEquals(new Point(628, 200), BoxPlace.of(new Rectangle(700, 200, 24, 24), new Dimension(70, 300),
			new Dimension(800, 600)));
	}

	@Test
	public void t26b_clampsAtTheLeftEdge()
	{
		assertEquals(new Point(0, 50), BoxPlace.of(new Rectangle(30, 50, 24, 24), new Dimension(70, 40),
			new Dimension(800, 600)));
	}

	@Test
	public void t26c_noIconGoesToTheBottomRightCorner()
	{
		assertEquals(new Point(720, 550), BoxPlace.of(null, new Dimension(70, 40), new Dimension(800, 600)));
	}

	@Test
	public void t34a_aMessageSitsJustAboveTheIconRightEdgesLevel()
	{
		assertEquals(new Point(574, 176), BoxPlace.above(new Rectangle(700, 200, 24, 24), new Dimension(150, 22),
			new Dimension(800, 600)));
	}

	@Test
	public void t34b_noRoomAboveGoesBelow()
	{
		assertEquals(new Point(574, 36), BoxPlace.above(new Rectangle(700, 10, 24, 24), new Dimension(150, 22),
			new Dimension(800, 600)));
	}

	@Test
	public void t34c_aMessageNearTheLeftEdgeClampsToZero()
	{
		assertEquals(new Point(0, 176), BoxPlace.above(new Rectangle(20, 200, 24, 24), new Dimension(150, 22),
			new Dimension(800, 600)));
	}

	@Test
	public void t34d_noIconGoesToTheBottomRightCorner()
	{
		assertEquals(new Point(640, 568), BoxPlace.above(null, new Dimension(150, 22), new Dimension(800, 600)));
	}

	@Test
	public void t26d_aBoxBiggerThanTheCanvasClampsToZero()
	{
		assertEquals(new Point(0, 0), BoxPlace.of(null, new Dimension(900, 700), new Dimension(800, 600)));
	}
}
