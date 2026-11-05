package com.inventoryquickcheck;

import java.awt.Point;

/**
 * How many entries the box draws, and in how many columns and rows, for a layout. When the entries do not fit, one
 * place is given to {@code +<n>}, so one fewer entry is drawn than the limit. Pure.
 */
public final class BoxGrid
{
	private final int shown;
	private final int more;
	private final int columns;
	private final int rows;

	private BoxGrid(int shown, int more, int columns, int rows)
	{
		this.shown = shown;
		this.more = more;
		this.columns = columns;
		this.rows = rows;
	}

	public static BoxGrid of(int entries, BoxLayout layout)
	{
		if (entries <= 0)
		{
			return new BoxGrid(0, 0, 0, 0);
		}
		final int perColumn = layout.rowsPerColumn();
		final boolean limited = layout.maxColumns() > 0;
		final int limit = limited ? perColumn * layout.maxColumns() : Integer.MAX_VALUE;
		final int shown = entries > limit ? limit - 1 : entries;
		final int more = entries - shown;
		final int places = shown + (more > 0 ? 1 : 0);
		return new BoxGrid(shown, more, (places + perColumn - 1) / perColumn, Math.min(places, perColumn));
	}

	/** The entries drawn. */
	public int shown()
	{
		return shown;
	}

	/** The number after the plus sign; 0 when nothing is left out. */
	public int more()
	{
		return more;
	}

	public int columns()
	{
		return columns;
	}

	/** Rows of the tallest column. */
	public int rows()
	{
		return rows;
	}

	/** The column (x) and row (y) of a place; columns fill top to bottom. */
	public static Point place(int index, int rowsPerColumn)
	{
		return new Point(index / rowsPerColumn, index % rowsPerColumn);
	}
}
