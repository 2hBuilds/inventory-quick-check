package com.inventoryquickcheck;

/** How the box wraps a long list: rows per column and how many columns at most (0 = no limit). */
public enum BoxLayout
{
	ONE_COLUMN("One column", 8, 1),
	COLUMNS_OF_7("Columns of 7", 7, 0),
	TWO_COLUMNS_OF_7("Two columns of 7", 7, 2),
	TWO_COLUMNS_OF_10("Two columns of 10", 10, 2);

	private final String label;
	private final int rowsPerColumn;
	private final int maxColumns;

	BoxLayout(String label, int rowsPerColumn, int maxColumns)
	{
		this.label = label;
		this.rowsPerColumn = rowsPerColumn;
		this.maxColumns = maxColumns;
	}

	public int rowsPerColumn()
	{
		return rowsPerColumn;
	}

	/** The most columns the layout allows; 0 means no limit. */
	public int maxColumns()
	{
		return maxColumns;
	}

	/** The label RuneLite shows in the settings. */
	@Override
	public String toString()
	{
		return label;
	}
}
