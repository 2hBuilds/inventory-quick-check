package com.inventoryquickcheck;

/**
 * The outline colour preview: for a few seconds after the colour or thickness setting is saved, every potion carried
 * is outlined so the player can judge the choice. Pure: no client.
 */
final class ColourPreview
{
	static final long SHOW_MS = 5000;

	private ColourPreview()
	{
	}

	/** True while the preview that started at {@code startMs} (-1 for none) is still on. */
	static boolean showing(long startMs, long nowMs)
	{
		return startMs >= 0 && nowMs - startMs >= 0 && nowMs - startMs < SHOW_MS;
	}

	/** True for the two settings whose change starts a preview. */
	static boolean previews(String group, String key)
	{
		return "inventoryquickcheck".equals(group) && ("markColour".equals(key) || "markThickness".equals(key));
	}
}
