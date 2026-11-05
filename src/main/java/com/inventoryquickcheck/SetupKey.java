package com.inventoryquickcheck;

import com.google.common.hash.Hashing;

/**
 * The key Inventory Setups stores a setup under: {@code setupsV3_} plus the murmur3 128-bit hash of the setup's name,
 * in lowercase hex.
 */
public final class SetupKey
{
	/** Inventory Setups' config group. */
	public static final String GROUP = "inventorysetups";

	private SetupKey()
	{
	}

	/** The config key of the setup called {@code setupName}. */
	public static String of(String setupName)
	{
		return "setupsV3_" + Hashing.murmur3_128().hashUnencodedChars(setupName);
	}
}
