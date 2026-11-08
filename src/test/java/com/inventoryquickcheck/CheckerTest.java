package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CheckerTest
{
	private static final int BREW4 = 6685;
	private static final int BREW3 = 6687;
	private static final int BREW2 = 6689;
	private static final int BREW1 = 6691;
	private static final int RESTORE = 3024;
	private static final int SHARK = 385;
	private static final int BALL = 2;
	private static final int DUEL8 = 2552;
	private static final int DUEL3 = 2562;
	private static final int MIX2 = 11521;
	private static final int MIX1 = 11523;
	private static final int POUCH = 12791;
	private static final int BLOOD = 565;
	private static final int DEATH = 560;
	private static final int FIRE = 554;
	private static final int SOUL = 566;
	private static final int INFERNAL = 21295;
	private static final int FIRECAPE = 6570;

	private final ItemFacts facts = new FakeItemFacts();

	private static SetupItem it(int id)
	{
		return new SetupItem(id, 1, false);
	}

	private static SetupItem fz(int id)
	{
		return new SetupItem(id, 1, true);
	}

	private static List<SetupItem> slots(int size, Object... atIndexAndItem)
	{
		final List<SetupItem> list = new ArrayList<>(Collections.<SetupItem>nCopies(size, null));
		for (int i = 0; i < atIndexAndItem.length; i += 2)
		{
			list.set((Integer) atIndexAndItem[i], (SetupItem) atIndexAndItem[i + 1]);
		}
		return list;
	}

	private static SavedSetup setup(List<SetupItem> inv, List<SetupItem> eq, Integer... pouch)
	{
		return new SavedSetup(inv, eq, pouch.length == 0 ? null : Arrays.asList(pouch));
	}

	private static int[] ids(int size, int... atIndexAndId)
	{
		final int[] out = new int[size];
		Arrays.fill(out, -1);
		for (int i = 0; i < atIndexAndId.length; i += 2)
		{
			out[atIndexAndId[i]] = atIndexAndId[i + 1];
		}
		return out;
	}

	private static Carried carried(int[] inv, int[] worn, Set<Integer> pouch)
	{
		final int[] amounts = new int[28];
		Arrays.fill(amounts, 1);
		return new Carried(inv, amounts, worn, pouch);
	}

	private static Set<Integer> runes(Integer... ids)
	{
		return new HashSet<>(Arrays.asList(ids));
	}

	/** The T4 setup: brews, a shark, cannonballs, a rune pouch, the cape. */
	private static SavedSetup fullSetup()
	{
		return setup(slots(28, 0, it(BREW4), 1, it(BREW4), 2, it(SHARK), 3, it(BALL), 4, it(POUCH)),
			slots(14, 1, it(INFERNAL)), BLOOD, DEATH);
	}

	private static Carried fullCarried()
	{
		return carried(ids(28, 0, BREW4, 1, BREW4, 2, SHARK, 3, BALL, 4, POUCH), ids(14, 1, INFERNAL),
			runes(BLOOD, DEATH));
	}

	private void assertLines(CheckResult result, String... lines)
	{
		final List<String> actual = new ArrayList<>();
		for (final CheckResult.Entry entry : result.entries())
		{
			switch (entry.kind)
			{
				case MISSING:
					actual.add(entry.count > 1 ? "Missing: " + entry.count + " " + entry.name : "Missing: " + entry.name);
					break;
				case NOT_WORN:
					actual.add("Not worn: " + entry.name);
					break;
				default:
					actual.add("Rune pouch: no " + entry.name);
					break;
			}
		}
		if (!result.lowSlots().isEmpty())
		{
			actual.add("Low doses: " + result.lowSlots().size());
		}
		if (result.allGood())
		{
			actual.add("All good");
		}
		assertEquals(Arrays.asList(lines), actual);
	}

	@Test
	public void t4_everythingCarriedIsAllGood()
	{
		final CheckResult result = Checker.check(fullSetup(), fullCarried(), facts);
		assertLines(result, "All good");
		assertTrue(result.allGood());
		assertTrue(result.lowSlots().isEmpty());
	}

	@Test
	public void t5_lowDosesAreMarked()
	{
		final SavedSetup setup = setup(slots(28, 0, it(BREW4), 1, it(BREW4), 2, it(BREW4), 3, it(BREW4), 4, it(BREW4),
			5, it(BREW4)), slots(14));
		final Carried carried = carried(ids(28, 0, BREW4, 1, BREW4, 2, BREW4, 3, BREW4, 10, BREW3, 20, BREW1),
			ids(14), null);
		final CheckResult result = Checker.check(setup, carried, facts);
		assertEquals(new TreeSet<>(Arrays.asList(10, 20)), result.lowSlots());
		assertLines(result, "Low doses: 2");
		assertFalse(result.allGood());
	}

	@Test
	public void t6_missingPotionsAreCounted()
	{
		final SavedSetup setup = setup(slots(28, 0, it(BREW4), 1, it(BREW4), 2, it(BREW4), 3, it(BREW4), 4, it(BREW4),
			5, it(BREW4)), slots(14));
		final Carried carried = carried(ids(28, 0, BREW4, 1, BREW4, 2, BREW4, 3, BREW4), ids(14), null);
		assertLines(Checker.check(setup, carried, facts), "Missing: 2 Saradomin brew");
	}

	@Test
	public void t7_slotOrderIsIgnored()
	{
		final Carried carried = carried(ids(28, 27, BREW4, 20, BREW4, 10, SHARK, 5, BALL, 0, POUCH), ids(14, 1, INFERNAL),
			runes(BLOOD, DEATH));
		assertLines(Checker.check(fullSetup(), carried, facts), "All good");
	}

	@Test
	public void t8_fuzzyAcceptsAnyVariation()
	{
		final Carried carried = carried(ids(28, 0, DUEL3), ids(14), null);
		assertLines(Checker.check(setup(slots(28, 0, fz(DUEL8)), slots(14)), carried, facts), "All good");
		assertLines(Checker.check(setup(slots(28, 0, it(DUEL8)), slots(14)), carried, facts),
			"Missing: Ring of dueling(8)");
	}

	@Test
	public void t9_stackablesIgnoreAmounts()
	{
		final SavedSetup setup = setup(slots(28, 0, new SetupItem(BALL, 216, false)), slots(14));
		final int[] amounts = new int[28];
		amounts[0] = 5;
		assertLines(Checker.check(setup, new Carried(ids(28, 0, BALL), amounts, ids(14), null), facts), "All good");
		assertLines(Checker.check(setup, carried(ids(28), ids(14), null), facts), "Missing: Cannonball");
	}

	@Test
	public void t10_missingRuneIsNamed()
	{
		final SavedSetup setup = setup(slots(28, 0, it(POUCH)), slots(14), BLOOD, DEATH, FIRE, SOUL);
		final Carried carried = carried(ids(28, 0, POUCH), ids(14), runes(BLOOD, FIRE, SOUL));
		assertLines(Checker.check(setup, carried, facts), "Rune pouch: no Death rune");
	}

	@Test
	public void t11_noPouchCarriedSaysOnlyMissingPouch()
	{
		final SavedSetup setup = setup(slots(28, 0, it(POUCH)), slots(14), BLOOD, DEATH);
		final CheckResult result = Checker.check(setup, carried(ids(28), ids(14), null), facts);
		assertLines(result, "Missing: Rune pouch");
		assertEquals(12791, result.entries().get(0).itemId);
	}

	@Test
	public void t12_wrongCapeIsNotWorn()
	{
		final SavedSetup setup = setup(slots(28), slots(14, 1, it(INFERNAL)));
		final Carried carried = carried(ids(28), ids(14, 1, FIRECAPE, 0, SHARK, 5, SHARK), null);
		assertLines(Checker.check(setup, carried, facts), "Not worn: Infernal cape");
	}

	@Test
	public void t13_manyProblemsAreCut()
	{
		final SavedSetup setup = setup(slots(28, 0, it(SHARK), 1, it(SHARK), 2, it(BALL), 3, it(POUCH), 4, it(BREW4),
			5, it(RESTORE)), slots(14, 1, it(INFERNAL)), BLOOD, DEATH, FIRE, SOUL);
		final Carried carried = carried(ids(28, 0, POUCH, 7, BREW3), ids(14, 1, FIRECAPE), runes(BLOOD, FIRE));
		final CheckResult result = Checker.check(setup, carried, facts);
		assertLines(result, "Not worn: Infernal cape", "Missing: Cannonball", "Rune pouch: no Death rune",
			"Rune pouch: no Soul rune", "Missing: 2 Shark", "Missing: Super restore", "Low doses: 1");
		assertEquals(new TreeSet<>(Collections.singletonList(7)), result.lowSlots());
	}

	@Test
	public void t14_extraItemsAreIgnored()
	{
		final Carried carried = carried(ids(28, 0, BREW4, 1, BREW4, 2, SHARK, 3, BALL, 4, POUCH, 5, SHARK),
			ids(14, 1, INFERNAL), runes(BLOOD, DEATH));
		assertLines(Checker.check(fullSetup(), carried, facts), "All good");
	}

	@Test
	public void c1_potionsTheSetupDoesNotListAreOutlinedWhenNotFull()
	{
		final SavedSetup setup = setup(slots(28, 0, it(BREW4), 1, it(BREW4)), slots(14));
		final Carried carried = carried(ids(28, 0, BREW4, 1, BREW4, 2, MIX1, 3, RESTORE), ids(14), null);
		assertEquals(new TreeSet<>(Collections.singletonList(2)), Checker.check(setup, carried, facts).lowSlots());
	}

	@Test
	public void c2_aDoseTheSetupAsksForIsNotOutlined()
	{
		final SavedSetup setup = setup(slots(28, 0, it(BREW3)), slots(14));
		final Carried carried = carried(ids(28, 0, BREW3, 1, MIX1), ids(14), null);
		assertEquals(new TreeSet<>(Collections.singletonList(1)), Checker.check(setup, carried, facts).lowSlots());
	}

	@Test
	public void t15_spareLowDosesAreMarkedToo()
	{
		// The user, 2026-11-04: "mark all low" - a spare (2) beside two full brews flashes as well.
		final SavedSetup setup = setup(slots(28, 0, it(BREW4), 1, it(BREW4)), slots(14));
		final Carried carried = carried(ids(28, 0, BREW4, 1, BREW4, 2, BREW2), ids(14), null);
		final CheckResult result = Checker.check(setup, carried, facts);
		assertEquals(new TreeSet<>(Collections.singletonList(2)), result.lowSlots());
		assertLines(result, "Low doses: 1");
	}

	@Test
	public void t28_looseRuneCountsWhenInThePouch()
	{
		final SavedSetup setup = setup(slots(28, 0, it(POUCH), 1, new SetupItem(DEATH, 5285, false)), slots(14));
		final Carried carried = carried(ids(28, 0, POUCH), ids(14), runes(DEATH));
		assertLines(Checker.check(setup, carried, facts), "All good");
	}

	@Test
	public void t29_pouchRuneCountsWhenLoose()
	{
		final SavedSetup setup = setup(slots(28, 0, it(POUCH)), slots(14), BLOOD, DEATH);
		final Carried carried = carried(ids(28, 0, POUCH, 5, DEATH), ids(14), runes(BLOOD));
		assertLines(Checker.check(setup, carried, facts), "All good");
	}

	@Test
	public void t30_sortedWhenNothingIsCarried()
	{
		final SavedSetup setup = setup(slots(28, 0, it(SHARK), 1, it(BALL), 2, it(POUCH), 3, it(FIRECAPE)),
			slots(14, 1, it(INFERNAL)));
		assertLines(Checker.check(setup, carried(ids(28), ids(14), null), facts), "Missing: Fire cape",
			"Not worn: Infernal cape", "Missing: Rune pouch", "Missing: Cannonball", "Missing: Shark");
	}

	@Test
	public void t16_lowerDoseOfAnotherPotionIsLow()
	{
		final SavedSetup setup = setup(slots(28, 0, it(MIX2)), slots(14));
		final CheckResult result = Checker.check(setup, carried(ids(28, 3, MIX1), ids(14), null), facts);
		assertEquals(new TreeSet<>(Collections.singletonList(3)), result.lowSlots());
		assertLines(result, "Low doses: 1");
	}

	@Test
	public void t32_entriesSayWhichIdsAnswerThem()
	{
		final SavedSetup setup = setup(slots(28, 0, it(BREW4), 1, it(BREW4), 2, it(SHARK)), slots(14));
		final CheckResult result = Checker.check(setup, carried(ids(28), ids(14), null), facts);
		final CheckResult.Entry brew = result.entries().get(0);
		assertEquals(new TreeSet<>(Arrays.asList(BREW4, BREW3, BREW2, BREW1)), brew.accepts);
		assertTrue(brew.dose);
		final CheckResult.Entry shark = result.entries().get(1);
		assertEquals(new TreeSet<>(Collections.singletonList(SHARK)), shark.accepts);
		assertFalse(shark.dose);
	}
}
