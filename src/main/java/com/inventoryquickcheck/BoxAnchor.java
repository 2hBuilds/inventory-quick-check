package com.inventoryquickcheck;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Where the Missing box hangs from: left of a rectangle with the tops level, or with its top left at a point (right
 * of the bank). Immutable.
 */
final class BoxAnchor
{
	private final Rectangle beside;
	private final Point topLeft;

	private BoxAnchor(Rectangle beside, Point topLeft)
	{
		this.beside = beside;
		this.topLeft = topLeft;
	}

	/** Hang left of the rectangle, tops level ({@link BoxPlace#of}). */
	static BoxAnchor beside(Rectangle anchor)
	{
		return new BoxAnchor(anchor == null ? null : new Rectangle(anchor), null);
	}

	/** Hang right of the bank: top left 4 px right of its frame, a quarter of the way down it. */
	static BoxAnchor rightOf(Rectangle bank)
	{
		return new BoxAnchor(null, new Point(bank.x + bank.width + 4, bank.y + bank.height / 4));
	}

	/** The box's top left for its size on a canvas. */
	Point place(Dimension box, Dimension canvas)
	{
		if (topLeft != null)
		{
			return new Point(topLeft);
		}
		return BoxPlace.of(beside, box, canvas);
	}
}
