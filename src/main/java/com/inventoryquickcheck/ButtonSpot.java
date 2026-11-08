package com.inventoryquickcheck;

import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.api.gameval.InterfaceID;

/** Where the check button goes relative to the bank-side inventory's slots. Pure functions; no client. */
final class ButtonSpot
{
	/** The game's row pitch of the inventory, in pixels. */
	private static final int DEFAULT_PITCH = 36;
	/** The game's gap between two inventory slots side by side, in pixels. */
	private static final int DEFAULT_GAP = 6;

	private ButtonSpot()
	{
	}

	/**
	 * The gap between the first slot and the one beside it: the second's left edge minus the first's right edge.
	 * Choice: anything unreadable, or overlapping, gives the game's own 6.
	 */
	static int gap(Rectangle slot0, Rectangle slot1)
	{
		if (slot0 == null || slot1 == null || slot1.x - (slot0.x + slot0.width) < 0)
		{
			return DEFAULT_GAP;
		}
		return slot1.x - (slot0.x + slot0.width);
	}

	/** The small icon's side inside the first slot: half the slot's height (16 at the game's 32). */
	static int smallSize(Rectangle slot0)
	{
		return slot0.height / 2;
	}

	/**
	 * The row pitch: the second row's y minus the first's. Choice: anything unreadable, or not below the first
	 * slot, gives the game's own 36.
	 */
	static int pitch(Rectangle slot0, Rectangle slot4)
	{
		if (slot0 == null || slot4 == null || slot4.y - slot0.y <= 0)
		{
			return DEFAULT_PITCH;
		}
		return slot4.y - slot0.y;
	}

	/** The skills tab above the inventory for a toplevel, or -1 where there is none (modern layouts). */
	static int skillsStoneOf(int toplevel)
	{
		if (toplevel == InterfaceID.TOPLEVEL)
		{
			return InterfaceID.Toplevel.STONE1;
		}
		if (toplevel == InterfaceID.TOPLEVEL_OSRS_STRETCH)
		{
			return InterfaceID.ToplevelOsrsStretch.STONE1;
		}
		return -1;
	}

	/** A tab's centre (either tab) as an offset from the first slot's top left; null when a part or the tab is missing. */
	static Point tabOffset(Rectangle tab, Rectangle slot0)
	{
		if (tab == null || slot0 == null || tab.width <= 0 || tab.height <= 0)
		{
			return null;
		}
		return new Point(tab.x + tab.width / 2 - slot0.x, tab.y + tab.height / 2 - slot0.y);
	}

	/** The size x size square centred on the first slot's top left plus the offset; null when either is null. */
	static Rectangle tabSpot(Rectangle slot0, Point offset, int size)
	{
		if (slot0 == null || offset == null)
		{
			return null;
		}
		return new Rectangle(slot0.x + offset.x - size / 2, slot0.y + offset.y - size / 2, size, size);
	}

	/** The icon's top left above the inventory: on the skills tab's spot given an offset, else over the column. */
	static Point aboveInventory(Rectangle slot0, int pitch, Point skillsOffset, int size)
	{
		final int cx;
		final int cy;
		if (skillsOffset != null)
		{
			cx = slot0.x + skillsOffset.x;
			cy = slot0.y + skillsOffset.y;
		}
		else
		{
			cx = slot0.x + slot0.width / 2;
			cy = slot0.y + slot0.height / 2 - Math.round(1.25f * pitch);
		}
		return new Point(cx - size / 2, cy - size / 2);
	}

	/**
	 * The point the Missing box hangs left of on the modern layout: the first slot's left edge moved two slot columns
	 * left (the pitch of one column is the slot's width plus the gap), the top level with the slot, so the Status Bars
	 * between the inventory and the box do not cover it.
	 */
	static Rectangle leftOfModernInventory(Rectangle slot0, int columnPitch)
	{
		return new Rectangle(slot0.x - 2 * columnPitch, slot0.y, 0, 0);
	}

	/**
	 * The icon's top left inside the first slot: centred half a slot gap left of the slot's left edge, level with its
	 * top edge (the user, 2026-11-08: "middle x of two inv spaces ... roughly where i drew the orange circle").
	 */
	static Point insideFirstSlot(Rectangle slot0, int gap, int size)
	{
		return new Point(slot0.x - gap / 2 - size / 2, slot0.y - size / 2);
	}
}
