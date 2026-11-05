package com.inventoryquickcheck;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

/**
 * The variation groups come from RuneLite's own item data, keyed by base id; asking with a lower dose must still give
 * the whole group (the user, 2026-11-05: "still no flashing on the pots that are not full"). The item manager is not
 * needed for variations, so none is given.
 */
public class ClientItemFactsTest
{
	private static final int PRAYER_4 = 2434;
	private static final int PRAYER_3 = 139;
	private static final int PRAYER_2 = 141;
	private static final int PRAYER_1 = 143;

	private final ClientItemFacts facts = new ClientItemFacts(null);

	@Test
	public void v1_aLowerDoseGetsTheWholeGroup()
	{
		final Collection<Integer> group = facts.variations(PRAYER_3);
		assertTrue(group.toString(), group.containsAll(Arrays.asList(PRAYER_4, PRAYER_3, PRAYER_2, PRAYER_1)));
	}

	@Test
	public void v2_theBaseGetsTheWholeGroup()
	{
		final Collection<Integer> group = facts.variations(PRAYER_4);
		assertTrue(group.toString(), group.containsAll(Arrays.asList(PRAYER_4, PRAYER_3, PRAYER_2, PRAYER_1)));
	}

	@Test
	public void v3_theItemItselfIsAlwaysThere()
	{
		assertTrue(facts.variations(PRAYER_1).contains(PRAYER_1));
	}
}
