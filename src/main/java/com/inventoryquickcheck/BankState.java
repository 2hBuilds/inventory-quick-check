package com.inventoryquickcheck;

import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

/** Whether the bank is open. Client thread only. */
public final class BankState
{
	private BankState()
	{
	}

	public static boolean isOpen(Client client)
	{
		final Widget items = client.getWidget(InterfaceID.Bankmain.ITEMS);
		return items != null && !items.isHidden();
	}
}
