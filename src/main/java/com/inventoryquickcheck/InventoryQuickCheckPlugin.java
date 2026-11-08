package com.inventoryquickcheck;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Collections;
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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
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
	private ColorPickerManager colorPickerManager;

	@Inject
	private InventoryQuickCheckConfig config;

	private volatile ShownResult shown;
	private volatile Map<Integer, Integer> previewSlots = new HashMap<>();
	private volatile long previewStartMs = -1;
	/** The colour under the picker's hand while it is open, else null. */
	private volatile Color liveColour;
	private OutlinePicker outlinePicker;
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
		outlinePicker = new OutlinePicker(colorPickerManager, client);
		resultOverlay = new ResultOverlay(() -> shown, () -> shown = null, config, place -> checkButton.boxSpot(place),
			client);
		marksOverlay = new MarksOverlay(() -> shown, this::previewNow, this::markColourNow, config,
			itemManager);
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
		checkButton = new CheckButton(this, client, config, tooltipManager, () -> shown,
			this::openOutlinePicker);
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
		previewSlots = new HashMap<>();
		previewStartMs = -1;
		liveColour = null;
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

	/** The potions to outline for the colour preview right now: empty when it is not showing. */
	private Map<Integer, Integer> previewNow()
	{
		return liveColour != null || ColourPreview.showing(previewStartMs, System.currentTimeMillis())
			? previewSlots : Collections.<Integer, Integer>emptyMap();
	}

	/** The outline colour to draw: the picker's live colour while it is open, else the saved one. */
	private Color markColourNow()
	{
		final Color live = liveColour;
		return live != null ? live : config.markColour();
	}

	/** Every potion carried, inventory slot to item id. Client thread. */
	private Map<Integer, Integer> potionSlots()
	{
		final int[] ids = CarriedReader.read(client).inventoryIds;
		final Map<Integer, Integer> potions = new HashMap<>();
		for (final int slot : LowPotions.potionSlots(ids, facts))
		{
			potions.put(slot, ids[slot]);
		}
		return potions;
	}

	/**
	 * The button menu's Outline colour line: opens RuneLite's colour picker; every potion carried is outlined in the
	 * colour under the reader's hand, and the colour is saved when the picker closes.
	 */
	private void openOutlinePicker()
	{
		clientThread.invoke(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return;
			}
			previewSlots = potionSlots();
			outlinePicker.open(config.markColour(), this::liveMove, this::liveDone);
		});
	}

	private void liveMove(Color colour)
	{
		liveColour = colour;
		clientThread.invoke(() ->
		{
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				previewSlots = potionSlots();
			}
		});
	}

	private void liveDone(Color colour)
	{
		if (colour != null)
		{
			configManager.setConfiguration(InventoryQuickCheckConfig.GROUP, "markColour", colour);
		}
		liveColour = null;
	}

	/** Starts the colour preview (five seconds) when the outline colour or thickness is saved. */
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (resultOverlay != null && "runelite".equals(event.getGroup())
			&& (resultOverlay.getName() + "_preferredLocation").equals(event.getKey()))
		{
			// RuneLite saved (a drag) or unset (Alt+right-click) the box's own spot.
			final MissingBoxPlace now = config.boxPlace();
			if (event.getNewValue() != null)
			{
				if (MissingBoxPlace.afterDrag(now) != null)
				{
					configManager.setConfiguration(InventoryQuickCheckConfig.GROUP, "boxPlaceBefore", now);
					configManager.setConfiguration(InventoryQuickCheckConfig.GROUP, "boxPlace",
						MissingBoxPlace.WHERE_I_DRAG);
					say(Texts.BOX_DRAGGED);
				}
			}
			else
			{
				final MissingBoxPlace back = MissingBoxPlace.afterReset(now, config.boxPlaceBefore());
				if (back != null)
				{
					configManager.setConfiguration(InventoryQuickCheckConfig.GROUP, "boxPlace", back);
					say(Texts.BOX_BACK + back + ".");
				}
			}
			return;
		}
		if (InventoryQuickCheckConfig.GROUP.equals(event.getGroup()) && "boxPlace".equals(event.getKey())
			&& resultOverlay != null)
		{
			final MissingBoxPlace place = config.boxPlace();
			resultOverlay.apply(place);
			if (place != MissingBoxPlace.WHERE_I_DRAG)
			{
				// A picked fixed choice (or Hidden) drops a dragged spot.
				overlayManager.resetOverlay(resultOverlay);
			}
			return;
		}
		if (!ColourPreview.previews(event.getGroup(), event.getKey()))
		{
			return;
		}
		clientThread.invoke(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return;
			}
			previewSlots = potionSlots();
			previewStartMs = System.currentTimeMillis();
		});
	}

	/** A game chat line, on the client thread (the panel cannot show a setting a plugin changed until reopened). */
	private void say(String line)
	{
		clientThread.invoke(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", line, null));
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
