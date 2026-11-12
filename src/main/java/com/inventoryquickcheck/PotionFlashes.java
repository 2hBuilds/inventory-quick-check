package com.inventoryquickcheck;

import java.awt.Color;

/**
 * How many times the outline on potions flashes after a check. A flash is one off-then-on (the user, 2026-11-11:
 * "perhaps we should consider a flash as an off on action?"), so Steady never goes off and 1 flash is the look the
 * plugin always had. RuneLite's settings dropdown shows an enum through its {@code toString()} in a label that draws
 * HTML, so each choice shows as a small bar of its pattern, in the outline colour (the user, 2026-11-12), then its
 * name; the saved setting is the constant's name. The bar is a row of coloured table cells, since a picture file
 * cannot change colour.
 */
public enum PotionFlashes
{
	STEADY("Steady", 0),
	ONE("1 flash", 1),
	TWO("2 flashes", 2),
	THREE("3 flashes", 3),
	FIVE("5 flashes", 5),
	KEEP_FLASHING("Nonstop", -1);

	private static final int BAR_WIDTH = 42;
	private static final int LIT_WIDTH = 4;
	private static final int DARK_WIDTH = 3;
	private static final int KEEP_FLASHING_CYCLES = BAR_WIDTH / (LIT_WIDTH + DARK_WIDTH);
	private static final String DARK = "#505050";

	/** The outline colour the bars are drawn in; the plugin sets it from the settings. */
	private static volatile Color colour = Color.RED;

	private final String label;
	private final int count;

	PotionFlashes(String label, int count)
	{
		this.label = label;
		this.count = count;
	}

	/** Draws the bars in this colour from now on. */
	public static void useColour(Color outline)
	{
		colour = outline;
	}

	/** The plain name of the choice. */
	public String label()
	{
		return label;
	}

	/** How many off-then-on flashes; negative for Nonstop. */
	public int count()
	{
		return count;
	}

	/** The bar's cell widths in pixels, lit first and then turn about, {@value #BAR_WIDTH} in all. */
	int[] runs()
	{
		final int cycles = count < 0 ? KEEP_FLASHING_CYCLES : count;
		final int tail = BAR_WIDTH - cycles * (LIT_WIDTH + DARK_WIDTH);
		final int[] runs = new int[cycles * 2 + (tail > 0 ? 1 : 0)];
		for (int i = 0; i < cycles; i++)
		{
			runs[i * 2] = LIT_WIDTH;
			runs[i * 2 + 1] = DARK_WIDTH;
		}
		if (tail > 0)
		{
			runs[runs.length - 1] = tail;
		}
		return runs;
	}

	/** The settings-list text: the pattern bar in this colour, then the name. */
	String menuText(Color lit)
	{
		final String litHex = String.format("#%02x%02x%02x", lit.getRed(), lit.getGreen(), lit.getBlue());
		final StringBuilder text = new StringBuilder("<html><table cellpadding=0 cellspacing=0 border=0><tr>"
			+ "<td valign=middle><table cellpadding=0 cellspacing=0 border=0 style='font-size:6px'><tr>");
		final int[] runs = runs();
		for (int i = 0; i < runs.length; i++)
		{
			text.append("<td bgcolor=").append(i % 2 == 0 ? litHex : DARK).append(" width=").append(runs[i])
				.append("></td>");
		}
		return text.append("</tr></table></td><td>&nbsp;").append(label()).append("</td></tr></table></html>")
			.toString();
	}

	/** The text RuneLite shows in the settings dropdown: the bar in the outline colour, then the name. */
	@Override
	public String toString()
	{
		return menuText(colour);
	}
}
