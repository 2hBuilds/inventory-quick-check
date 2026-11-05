# 2h Inventory Setups Quick Check

> The full description of 2h Inventory Setups Quick Check. The short version, with pictures, is the plugin's page on the Plugin Hub (the repository's README).

One click at the bank checks what you carry against the setup open in the Inventory Setups plugin. What is missing shows in a small box beside the button, the bank items you need show how many to take out, and potions below the setup's dose are outlined. It is an add-on for Inventory Setups and is not made by its author.

## The button

A red disc with a white "!" sits on the attack tab while your bank is open. Hover it for "Check setup". A left click runs the check. Shift+right-click it for "Configure" (this plugin's settings) and "Discord" (opens the 2hBuilds Discord in your browser). Hold Alt and drag it to move it; Alt+right-click puts it back on the tab. The "Show the button" setting hides it.

## The hotkey

Set "Hotkey" in the settings to run the same check anywhere, bank open or not. The result then shows for "Seconds to show" (6 by default). At the bank it also clears when the bank closes. With no hotkey set (the default), only the button runs the check.

## What it checks

- **Inventory.** Every item in the setup's inventory must be somewhere in yours. Slot order does not matter. If you have fewer than the setup asks for, the item shows with how many more you need.
- **Worn items.** Each equipment slot in the setup must hold that item.
- **Potions.** Every potion you carry below the strongest dose the setup asks for that potion is outlined, spares included.
- **No setup.** With no setup open, or Inventory Setups off, it still outlines every potion that is not full: below the highest dose that potion comes in (4 for most, 2 for barbarian mixes). Jewellery with charges, such as a Games necklace(8), is left alone.
- **Rune pouch.** Each rune the setup puts in the pouch must be in your pouch or loose in your inventory. A rune the setup wants loose also counts when it is in your pouch.
- **Fuzzy items.** Where the setup marks an item fuzzy, any charge or dose of it counts.

It does not check how many of a stackable you carry (coins, runes, ammo count as present once you have some), and it ignores items the setup does not ask for.

## What you see

- **Missing.** A box beside the button, titled "Missing", lists each missing item as its picture with "x2", "x3" and so on beside it. Items you can wear come first, then other items, then stackables, then food and potions. The box keeps its right edge still as it shrinks. Hold Alt and drag it to move it; Alt+right-click puts it back.
- **Bank numbers.** Each bank item that answers a line of the box shows how many to take out, in bold orange at its bottom-right corner. For a potion the number goes on its strongest dose in your bank. An item to wear gets a 1 unless your inventory already holds it. Numbers for the same bank item add up.
- **Low potions.** Their outline flashes twice, and the second flash stays, in your inventory and in the bank's view of your inventory.
- **All good.** When nothing is missing, "All good" shows just above the button.
- **No setup.** With no setup open, or Inventory Setups off, a short "Potions only" shows just above the button when potions were outlined, or "Potions full" when none were. Hover the button for what to do: "No setup open: click View setup in the Inventory Setups plugin", or "For the full check, install or turn on the Inventory Setups plugin". A chat line also says so once per login when Inventory Setups is missing.

While a result is up, it updates as your inventory, worn items, rune pouch or bank change: the box shrinks, the numbers count down and the outlines follow. A result from the button stays until the bank closes.

## Settings

In RuneLite's settings, under 2h Inventory Setups Quick Check, or Shift+right-click the button and choose Configure.

| Setting | What it does |
|---|---|
| Show the button | Shows the check button on the attack tab while the bank is open. On by default. |
| Hotkey | Runs the check anywhere. Not set by default. |
| Mark colour | The colour of the outlines on low potions. Red by default. |
| Mark thickness | How thick the outline is, 1 to 3 pixels. 2 by default. |
| Seconds to show | How long a hotkey result stays up, 2 to 30. 6 by default. A button result stays until the bank closes. |
| Box layout | How a long list wraps: One column (the default: up to 8, then "+N"), Columns of 7, Two columns of 7, or Two columns of 10. |
| Show the missing box | Shows the box of missing things. "All good" and the setup messages show either way. On by default. |
| Number the bank items | Shows on each bank item how many to take out. On by default. |

## Privacy

It makes no network request and writes no files. It reads your inventory, worn items, rune pouch, bank and the setup open in Inventory Setups, and keeps nothing after the result clears. The Discord entry opens a link in your browser only when you click it. It does not act on the game.

## Licence

BSD 2-Clause. See `LICENSE`.
