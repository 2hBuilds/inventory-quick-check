package com.inventoryquickcheck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ColourPreviewTest
{
	@Test
	public void p2_showsForFiveSeconds()
	{
		assertTrue(ColourPreview.showing(1000, 1000));
		assertTrue(ColourPreview.showing(1000, 5999));
		assertFalse(ColourPreview.showing(1000, 6000));
		assertFalse(ColourPreview.showing(1000, 999));
		assertFalse(ColourPreview.showing(-1, 1000));
	}

	@Test
	public void p3_onlyTheColourAndThicknessPreview()
	{
		assertTrue(ColourPreview.previews("inventoryquickcheck", "markColour"));
		assertTrue(ColourPreview.previews("inventoryquickcheck", "markThickness"));
		assertFalse(ColourPreview.previews("inventoryquickcheck", "hotkey"));
		assertFalse(ColourPreview.previews("other", "markColour"));
	}
}
