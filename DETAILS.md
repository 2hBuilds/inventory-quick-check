# 2h Inventory Setups Quick Check

> The full description of 2h Inventory Setups Quick Check. The short version, with pictures, is the plugin's page on the Plugin Hub (the repository's README).

One click at the bank checks what you carry against the setup open in the Inventory Setups plugin. What is missing shows in a small box beside your inventory or your bank, the bank items you need show how many to take out, and potions that are not full are outlined. It is an add-on for Inventory Setups and is not made by its author.

## The button

A small picture (a gold ring with a tick by default) sits inside the top-left corner of your inventory's first slot while your bank is open. Hover it for "Check setup". A left click runs the check. Shift+right-click it for "Configure" (this plugin's settings), "Outline colour" (opens RuneLite's colour picker; every potion you carry is outlined in the colour as you move it, and the colour is saved when the picker closes) and "Discord" (opens the 2hBuilds Discord in your browser). "Button place" can put it above the inventory (on the skills tab in the fixed and resizable classic layouts) or on the attack tab instead. Where the box of missing things sits is the Box place setting (below). It never moves on its own. "Button look" picks its picture from thirteen (Gold ring check by default); each choice shows its picture in the settings list. The "Show the button" setting hides it.

## The hotkey

Set "Hotkey" in the settings to run the same check anywhere, bank open or not. The result then shows for "Seconds to show" (6 by default). At the bank it also clears when the bank closes. With no hotkey set (the default), only the button runs the check.

## What it checks

- **Inventory.** Every item in the setup's inventory must be somewhere in yours. Slot order does not matter. If you have fewer than the setup asks for, the item shows with how many more you need.
- **Worn items.** Each equipment slot in the setup must hold that item.
- **Potions.** Every potion you carry below the strongest dose the setup asks for that potion is outlined, spares included. A potion the setup does not list is outlined when it is not full.
- **No setup.** With no setup open, or Inventory Setups off, it still outlines every potion that is not full: below the highest dose that potion comes in (4 for most, 2 for barbarian mixes). Jewellery with charges, such as a Games necklace(8), is left alone.
- **Rune pouch.** Each rune the setup puts in the pouch must be in your pouch or loose in your inventory. A rune the setup wants loose also counts when it is in your pouch.
- **Fuzzy items.** Where the setup marks an item fuzzy, any charge or dose of it counts.

It does not check how many of a stackable you carry (coins, runes, ammo count as present once you have some), and it ignores items the setup does not ask for.

## What you see

- **Missing.** A box titled "Missing" lists each missing item as its picture with "x2", "x3" and so on beside it. Items you can wear come first, then other items, then stackables, then food and potions. The box keeps its right edge still as it shrinks. Where it sits is the Box place setting: Automatic (the default: right of the bank, or left of the inventory on the fixed layout, where the bank leaves no room beside it), left of the inventory, right of the bank, where you drag it (hold Alt and drag it; dragging switches the setting to this, and Alt+right-click puts it back; a chat line says so, because RuneLite's settings panel shows the change only when reopened), or hidden.
- **Bank numbers.** Each bank item that answers a line of the box shows how many to take out, in bold orange at its bottom-right corner. For a potion the number goes on its strongest dose in your bank. An item to wear gets a 1 unless your inventory already holds it. Numbers for the same bank item add up.
- **Low potions.** Their outline flashes twice, and the second flash stays, in your inventory and in the bank's view of your inventory.
- **All good.** When nothing is missing, "All good" shows just above the button.
- **No setup.** With no setup open, or Inventory Setups off, a short "Potions only" shows just above the button when potions were outlined, or "Potions full" when none were. Hover the button for what to do: "No setup open: click View setup in the Inventory Setups plugin", or "For the full check, install or turn on the Inventory Setups plugin". A chat line also says so once per login when Inventory Setups is missing.

While a result is up, it updates as your inventory, worn items, rune pouch or bank change: the box shrinks, the numbers count down and the outlines follow. A result from the button stays until the bank closes.

## Settings

In RuneLite's settings, under 2h Inventory Setups Quick Check, or Shift+right-click the button and choose Configure.

| Setting | What it does |
|---|---|
| Show the button | Shows the check button while the bank is open. On by default. |
| Button place | Where the button sits: Above the inventory, Inside the first slot, or Attack tab. Inside the first slot by default. |
| Button look | How the button looks, one of thirteen pictures, each shown in the settings list. Gold ring check by default. |
| Hotkey | Runs the check anywhere. Not set by default. |
| Outline colour | The colour of the outline on potions. Red by default. Picking a colour here shows it on every potion in your inventory for five seconds. For a live preview, Shift+right-click the button and choose Outline colour. |
| Outline thickness | How thick the outline is, 1 to 3 pixels. 2 by default. |
| Seconds to show | How long a hotkey result stays up, 2 to 30. 6 by default. A button result stays until the bank closes. |
| Box layout | How a long list wraps: One column (the default: up to 8, then "+N"), Columns of 7, Two columns of 7, or Two columns of 10. |
| Box place | Where the Missing box sits: Automatic (the default), Left of the inventory, Right of the bank, Where I drag it, or Hidden. Dragging it (Alt+drag) switches this to Where I drag it; Alt+right-click puts it back. |
| Number the bank items | Shows on each bank item how many to take out. On by default. |

## Privacy

It makes no network request and writes no files. It reads your inventory, worn items, rune pouch, bank and the setup open in Inventory Setups, and keeps nothing after the result clears. The Discord entry opens a link in your browser only when you click it. It does not act on the game.

## Licence

BSD 2-Clause. See `LICENSE`.
