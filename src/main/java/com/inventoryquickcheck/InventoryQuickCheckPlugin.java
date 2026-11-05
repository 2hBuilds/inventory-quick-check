package com.inventoryquickcheck;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 2h Inventory Setups Quick Check: checks the inventory against the setup open in Inventory Setups. No sidebar; its settings live in
 * RuneLite's own config panel. A hotkey runs the check; the answer shows in a box and the low potions are outlined.
 * Choice: the once-per-login chat line goes out through {@code client.addChatMessage} as a game message, because the
 * Hub's API guard refuses the chat message manager class in this package.
 */
@PluginDescriptor(
	name = "2h Inventory Setups Quick Check",
	internalName = "inventory-quick-check",
	description = "One click at the bank: shows what's missing from your Inventory Setups setup and rings low potions. (v" + Version.CURRENT + ")",
	tags = {"inventory", "setups", "check", "quick", "potions", "doses", "pots", "restock", "bank", "supplies"}
)
public class InventoryQuickCheckPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(InventoryQuickCheckPlugin.class);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EventBus eventBus;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private InventoryQuickCheckConfig config;

	private volatile ShownResult shown;
	private SetupSource source;
	private ClientItemFacts facts;
	private ResultOverlay resultOverlay;
	private MarksOverlay marksOverlay;
	private BankNumbersOverlay bankNumbersOverlay;
	private MessageOverlay messageOverlay;
	private CheckButton checkButton;
	private ButtonClicks buttonClicks;
	private HotkeyListener hotkeyListener;
	private boolean probeArmed;
	private boolean chatSaid;

	@Provides
	InventoryQuickCheckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryQuickCheckConfig.class);
	}

	@Override
	protected void startUp()
	{
		source = new SetupSource(eventBus::post, configManager::getConfiguration, gson);
		facts = new ClientItemFacts(itemManager);
		resultOverlay = new ResultOverlay(() -> shown, () -> shown = null, config, () -> checkButton.anchor(), client);
		marksOverlay = new MarksOverlay(() -> shown, config, itemManager);
		bankNumbersOverlay = new BankNumbersOverlay(() -> shown, config);
		messageOverlay = new MessageOverlay(() -> shown, config, () -> checkButton.anchor(), client);
		hotkeyListener = new HotkeyListener(config::hotkey)
		{
			@Override
			public void hotkeyPressed()
			{
				clientThread.invoke(() -> runCheck(false, System.currentTimeMillis(), BankState.isOpen(client)));
			}
		};
		checkButton = new CheckButton(this, client, config, tooltipManager, () -> shown);
		buttonClicks = new ButtonClicks(checkButton::drawn, () -> clientThread.invoke(() -> runCheck(true, System.currentTimeMillis(), BankState.isOpen(client))));
		overlayManager.add(resultOverlay);
		overlayManager.add(marksOverlay);
		overlayManager.add(bankNumbersOverlay);
		overlayManager.add(messageOverlay);
		overlayManager.add(checkButton);
		mouseManager.registerMouseListener(buttonClicks);
		keyManager.registerKeyListener(hotkeyListener);
		log.info("2h Inventory Setups Quick Check {} started on RuneLite {}", Version.CURRENT, RuneLiteProperties.getVersion());
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(hotkeyListener);
		overlayManager.remove(resultOverlay);
		mouseManager.unregisterMouseListener(buttonClicks);
		overlayManager.remove(checkButton);
		overlayManager.remove(marksOverlay);
		overlayManager.remove(bankNumbersOverlay);
		overlayManager.remove(messageOverlay);
		shown = null;
		probeArmed = false;
	}

	/**
	 * Runs the check. Client thread, logged in only. A re-run while a result is shown passes that result's start time,
	 * bank flag and button flag on, so the timer, the bank-close hiding and the blinking carry on unchanged.
	 */
	private void runCheck(boolean fromButton, long now, boolean atBank)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		final SetupAnswer answer = source.read();
		log.debug("check: answer {} ({})", answer.state(), answer.name());
		switch (answer.state())
		{
			case NOT_RUNNING:
				shown = ShownResult.noSetup(Texts.HINT_TURN_ON, lowPotions(), now, atBank, fromButton);
				break;
			case NO_SETUP:
				shown = ShownResult.noSetup(Texts.HINT_OPEN_SETUP, lowPotions(), now, atBank, fromButton);
				break;
			default:
				final Carried carried = CarriedReader.read(client);
				final CheckResult result = Checker.check(answer.setup(), carried, facts);
				final Map<Integer, Integer> withdraw = Withdraw.counts(result.entries(), BankReader.read(client),
					carried.inventoryIds, facts);
				shown = ShownResult.of(result, carried, itemManager::getImage, now, atBank, fromButton, withdraw);
				break;
		}
	}

	/** The inventory slots holding a potion that is not full, slot to item id, for a check with no setup. Client thread. */
	private Map<Integer, Integer> lowPotions()
	{
		final int[] ids = CarriedReader.read(client).inventoryIds;
		final Map<Integer, Integer> low = new HashMap<>();
		for (final int slot : LowPotions.slots(ids, facts))
		{
			low.put(slot, ids[slot]);
		}
		return low;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		final ShownResult current = shown;
		if (current != null && current.startedAtBank() && !BankState.isOpen(client))
		{
			shown = null;
		}
		if (probeArmed)
		{
			probeArmed = false;
			if (!chatSaid && source.read().state() == SetupAnswer.State.NOT_RUNNING)
			{
				chatSaid = true;
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", Texts.CHAT_NEEDS, null);
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INV || event.getContainerId() == InventoryID.WORN
			|| event.getContainerId() == InventoryID.BANK)
		{
			rerun();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		final int id = event.getVarbitId();
		if (id == VarbitID.RUNE_POUCH_TYPE_1 || id == VarbitID.RUNE_POUCH_TYPE_2 || id == VarbitID.RUNE_POUCH_TYPE_3
			|| id == VarbitID.RUNE_POUCH_TYPE_4)
		{
			rerun();
		}
	}

	/** While a result is shown, checks again with what is carried now. Client thread. */
	private void rerun()
	{
		final ShownResult current = shown;
		if (current != null)
		{
			runCheck(current.fromButton(), current.startMs(), current.startedAtBank());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			probeArmed = true;
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			chatSaid = false;
		}
	}
}
