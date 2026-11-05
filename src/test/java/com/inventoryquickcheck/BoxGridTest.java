package com.inventoryquickcheck;

import java.awt.Point;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BoxGridTest
{
	private static void check(int entries, BoxLayout layout, int shown, int more, int columns, int rows)
	{
		final BoxGrid grid = BoxGrid.of(entries, layout);
		assertEquals(layout + " shown", shown, grid.shown());
		assertEquals(layout + " more", more, grid.more());
		assertEquals(layout + " columns", columns, grid.columns());
		assertEquals(layout + " rows", rows, grid.rows());
	}

	@Test
	public void t25a_twentyNineEntriesUnderEachLayout()
	{
		check(29, BoxLayout.ONE_COLUMN, 7, 22, 1, 8);
		check(29, BoxLayout.COLUMNS_OF_7, 29, 0, 5, 7);
		check(29, BoxLayout.TWO_COLUMNS_OF_7, 13, 16, 2, 7);
		check(29, BoxLayout.TWO_COLUMNS_OF_10, 19, 10, 2, 10);
	}

	@Test
	public void t25b_threeEntriesAreOneShortColumn()
	{
		for (final BoxLayout layout : BoxLayout.values())
		{
			check(3, layout, 3, 0, 1, 3);
		}
	}

	@Test
	public void t25c_noEntriesGiveZeros()
	{
		for (final BoxLayout layout : BoxLayout.values())
		{
			check(0, layout, 0, 0, 0, 0);
		}
	}

	@Test
	public void t25d_exactlyTheLimitIsNotCut()
	{
		check(14, BoxLayout.TWO_COLUMNS_OF_7, 14, 0, 2, 7);
	}

	@Test
	public void t25e_columnsFillTopToBottom()
	{
		assertEquals(new Point(1, 1), BoxGrid.place(8, 7));
	}
}
