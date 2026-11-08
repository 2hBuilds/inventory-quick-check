package com.inventoryquickcheck;

import java.net.URL;

/**
 * How the check button looks: one picture per choice, shipped as resources. RuneLite's settings dropdown shows an
 * enum through its {@code toString()} in a label, and a label draws text that starts with {@code <html>} as HTML, so
 * each look's text is its small picture followed by its name; the saved setting is the constant's name, so the HTML
 * never reaches the stored config.
 * Choice: a picture that cannot be found leaves the label alone.
 */
public enum ButtonLook
{
	SLATE_CHECK("Slate check", "slate_check"),
	GOLD_RING_CHECK("Gold ring check", "gold_ring_check"),
	TEAL_CHECK("Teal check", "teal_check"),
	SAGE_QUESTION("Sage question", "sage_question"),
	POTION("Potion", "potion"),
	SMILEY("Smiley", "smiley"),
	BACKPACK("Backpack", "backpack"),
	STARBURST("Starburst", "starburst"),
	LIGHTNING("Lightning", "lightning"),
	GOLD_STAR("Gold star", "gold_star"),
	DARK_DOT_TICK("Dark dot tick", "dark_dot_tick"),
	STONE_TABLET("Stone tablet", "stone_tablet"),
	GOLD_COIN("Gold coin", "gold_coin");

	private static final String FOLDER = "/com/inventoryquickcheck/looks/";

	private final String label;
	private final String key;
	private final String menuText;

	ButtonLook(String label, String key)
	{
		this.label = label;
		this.key = key;
		this.menuText = menuText(label(), ButtonLook.class.getResource(smallPicture()));
	}

	/** The plain name of the look. */
	public String label()
	{
		return label;
	}

	/** The resource path of the 24 px picture. */
	public String picture()
	{
		return FOLDER + key + ".png";
	}

	/** The resource path of the 16 px picture. */
	public String smallPicture()
	{
		return FOLDER + key + "_16.png";
	}

	/** The label alone when there is no picture, else the picture then the label as the HTML a settings label shows. */
	static String menuText(String label, URL picture)
	{
		if (picture == null)
		{
			return label;
		}
		return "<html><img src='" + picture + "' width=16 height=16>&nbsp;" + label + "</html>";
	}

	/** The text RuneLite shows in the settings dropdown: the small picture, then the name. */
	@Override
	public String toString()
	{
		return menuText;
	}
}
