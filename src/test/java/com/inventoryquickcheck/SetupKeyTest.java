package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SetupKeyTest
{
	@Test
	public void keysMatchInventorySetups()
	{
		assertEquals("setupsV3_198dafc9235845019f16c9e7aa6bf30e", SetupKey.of("Coli"));
		assertEquals("setupsV3_7b51ae49eabedff347e2e02ac591791a", SetupKey.of("inferno"));
	}
}
