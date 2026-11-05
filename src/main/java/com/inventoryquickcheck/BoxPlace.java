package com.inventoryquickcheck;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Where the result box goes by default: directly left of the check button's icon, tops level; with no icon to hang
 * from, 10 px in from the canvas's bottom-right corner. Never off the canvas's top or left.
 */
final class BoxPlace
{
	private static final int GAP = 2;
	private static final int MARGIN = 10;

	private BoxPlace()
	{
	}

	static Point of(Rectangle anchor, Dimension box, Dimension canvas)
	{
		if (anchor != null)
		{
			return new Point(Math.max(0, anchor.x - GAP - box.width), Math.max(0, anchor.y));
		}
		return corner(box, canvas);
	}

	/**
	 * Where a one-line message goes: just above the check button's icon, its right edge level with the icon's right
	 * edge (so it grows to the left, over the game, never off the right side); below the icon when there is no room
	 * above; with no icon, the bottom-right corner as for the box (the user, 2026-11-05: "show that text always just
	 * above where the current placement of the button is? even if we drag?").
	 */
	static Point above(Rectangle anchor, Dimension message, Dimension canvas)
	{
		if (anchor == null)
		{
			return corner(message, canvas);
		}
		final int x = Math.max(0, anchor.x + anchor.width - message.width);
		final int y = anchor.y - GAP - message.height;
		return new Point(x, y >= 0 ? y : anchor.y + anchor.height + GAP);
	}

	private static Point corner(Dimension box, Dimension canvas)
	{
		return new Point(Math.max(0, canvas.width - MARGIN - box.width),
			Math.max(0, canvas.height - MARGIN - box.height));
	}
}
