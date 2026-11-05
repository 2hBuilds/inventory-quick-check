package com.inventoryquickcheck;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ShownResultTest
{
	private static CheckResult.Entry entry(CheckResult.Kind kind, int id, int count, String name)
	{
		return new CheckResult.Entry(kind, id, count, name, Collections.singleton(id), false);
	}

	private static Carried carried()
	{
		final int[] ids = new int[28];
		Arrays.fill(ids, -1);
		ids[3] = 6685;
		final int[] amounts = new int[28];
		Arrays.fill(amounts, 1);
		final int[] worn = new int[14];
		Arrays.fill(worn, -1);
		return new Carried(ids, amounts, worn, null);
	}

	private static ShownResult shown(CheckResult result, List<Integer> asked, boolean atBank, boolean fromButton)
	{
		return ShownResult.of(result, carried(), id ->
		{
			asked.add(id);
			return null;
		}, 7L, atBank, fromButton, Collections.<Integer, Integer>emptyMap());
	}

	@Test
	public void t27_ofKeepsTheGivenStartBankAndButtonFlags()
	{
		final CheckResult result = new CheckResult(new TreeSet<>(), Collections.<CheckResult.Entry>emptyList());
		final ShownResult shown = ShownResult.of(result, carried(), id -> null, 1234L, false, true,
			Collections.<Integer, Integer>emptyMap());
		assertEquals(1234L, shown.startMs());
		assertFalse(shown.startedAtBank());
		assertTrue(shown.fromButton());
	}

	@Test
	public void t24a_rowsFollowTheEntriesWithTheirCounts()
	{
		final CheckResult result = new CheckResult(new TreeSet<>(Arrays.asList(1, 2, 3)), Arrays.asList(
			entry(CheckResult.Kind.MISSING, 6685, 3, "Saradomin brew"),
			entry(CheckResult.Kind.MISSING, 23685, 1, "Divine super combat potion"),
			entry(CheckResult.Kind.NOT_IN_POUCH, 30843, 1, "Aether rune")));
		final List<Integer> asked = new ArrayList<>();
		final ShownResult shown = shown(result, asked, false, false);
		assertEquals(3, shown.rows().size());
		assertEquals(6685, shown.rows().get(0).itemId);
		assertEquals(3, shown.rows().get(0).count);
		assertEquals(23685, shown.rows().get(1).itemId);
		assertEquals(1, shown.rows().get(1).count);
		assertEquals(30843, shown.rows().get(2).itemId);
		assertEquals(1, shown.rows().get(2).count);
		assertNull(shown.message());
		assertEquals(Arrays.asList(6685, 23685, 30843), asked);
	}

	@Test
	public void t24b_allGoodIsOneGreenMessage()
	{
		final ShownResult shown = shown(new CheckResult(new TreeSet<>(), Collections.<CheckResult.Entry>emptyList()),
			new ArrayList<>(), false, false);
		assertTrue(shown.rows().isEmpty());
		assertEquals("All good", shown.message());
		assertEquals(Color.GREEN, shown.messageColour());
	}

	@Test
	public void t24c_onlyLowSlotsHaveNoRowsAndNoMessage()
	{
		final ShownResult shown = shown(new CheckResult(new TreeSet<>(Arrays.asList(3)),
			Collections.<CheckResult.Entry>emptyList()), new ArrayList<>(), false, false);
		assertTrue(shown.rows().isEmpty());
		assertNull(shown.message());
		assertEquals(Collections.singletonMap(3, 6685), shown.lowSlots());
	}

	@Test
	public void t24d_theFlagsAreCarriedThrough()
	{
		final CheckResult none = new CheckResult(new TreeSet<>(), Collections.<CheckResult.Entry>emptyList());
		final ShownResult a = shown(none, new ArrayList<>(), true, true);
		assertTrue(a.startedAtBank());
		assertTrue(a.fromButton());
		assertEquals(7L, a.startMs());
		final ShownResult b = shown(none, new ArrayList<>(), false, false);
		assertFalse(b.startedAtBank());
		assertFalse(b.fromButton());
	}

	@Test
	public void t24f_theWithdrawMapIsCarriedThrough()
	{
		final CheckResult none = new CheckResult(new TreeSet<>(), Collections.<CheckResult.Entry>emptyList());
		final ShownResult shown = ShownResult.of(none, carried(), id -> null, 1L, true, false,
			Collections.singletonMap(6685, 3));
		assertEquals(Collections.singletonMap(6685, 3), shown.withdraw());
		assertTrue(ShownResult.notReady(Texts.TURN_ON, 5L, true, true).withdraw().isEmpty());
	}

	@Test
	public void t24e_notReadyIsOneWhiteMessage()
	{
		final ShownResult shown = ShownResult.notReady(Texts.TURN_ON, 5L, true, true);
		assertTrue(shown.rows().isEmpty());
		assertEquals(Texts.TURN_ON, shown.message());
		assertEquals(Color.WHITE, shown.messageColour());
		assertTrue(shown.lowSlots().isEmpty());
		assertTrue(shown.startedAtBank());
		assertTrue(shown.fromButton());
	}
}
