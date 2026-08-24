# Upcoming
* Learned curses are now shared between players on the same scoreboard team
* Vibration Detection's glow is now only visible to the entity wearing the rune, instead of everyone
* Fixed EMI integration breaking EMI
* Fixed sockets not correctly reset when removing curses
* Sockets granted by curses are now calculated on the fly instead of being stored on the item
* Added `rune fix_sockets` sub command to fix items with an incorrect stored sockets value, optionally targeting one or more players

# 1.2.0.1-alpha
* Startup crash fix

# 1.2.0.0-alpha
* Added rune reforging
  * Craft an Encased Rune with 2 runes and 7 lapis lazuli blocks, then smelt it in a furnace for 10 minutes to get a random rune
* Added Lapis-fueled (all durability items)
  * Chance to not consume durability, at the cost of consuming lapis lazuli from the inventory
* Added Padding (weapons)
  * Reduces knockback dealt by weapons
* Added Smokescreen (helmet)
  * Reduces the range at which mobs can detect you
* Added Curse of Exposure (helmet)
  * Increases the range at which mobs can detect you
* Added Vibration Detection (helmet)
  * Makes nearby living entities glow through walls for a moment when they emit a sound, including yourself
* Added Telekinesis (tools, weapons, trident, bow, crossbow and shears)
  * Sends drops from mined blocks and killed mobs directly to your inventory
* Added Hunter (bows and crossbows)
  * Increases arrow damage the farther it travels, with a starting penalty at point-blank range
* Added Curse of Undead (armor)
  * Set on fire the wearer under the sun
* Added Curse of Inaccuracy (bows and crossbows)
  * Increases the spread of arrows shot from bows and crossbows
* Added Curse of Slipperiness (tools and weapons)
  * Chance for the item to slip off your hand and fall to the ground when it loses durability
* Luck can now be applied to bows and crossbows
  * And having a bow or crossbow equipped is no longer needed for luck to apply
* Maces now count as weapons for runes
* Curses' attribute modifiers are now hidden if the curse is unknown
* Added tooltip infos to all the missing runes, even if them added attribute modifiers
* Fixed Atmospheric rune info not working
* Fixed Curse of Ender teleporting even when the damage was fully blocked with a shield
* Fixed Tunneling's client-side block outline not matching the blocks actually mined on the server

# 1.1.1.0
* Reduced Healthy bonus health
* Curses can now be extracted from items by default

# 1.1.0.0
* Curses can now be learned by using the tool they are applied to
* Runes now work on thrown tridents

# 1.0.2.1
* Added "Damage reduction: " infos to all damage reduction runes
* Fixed healthy not stacking on multiple pieces
* Fixed jump meter not raising the GUI elements when rendering

# 1.0.2.0
* Extra infos 
  * Now always report best values and not current or stacked ones
    * E.g. Vindication no longer shows accumulated damage
* Veining and Tunneling Runes' mining speed penalty is now configurable
* Slightly increased Thorns chance to trigger
* Increased Atmospheric under rain durability consumption reduction
* Fixed Feather Falling applicable to any armor piece

# 1.0.1.3-beta
* Rune items now show which items they're compatible with in their tooltip, based on the `runeenchanting:display_on_rune` item tag

# 1.0.1.2-beta
BREAKING CHANGE:
* Fixed rune registry being plural
  * This changes the rune tags folder from `data/runeenchanting/tags/runeenchanting/runes` to `data/runeenchanting/tags/runeenchanting/rune`

# 1.0.1.1-beta
* Fixed juicy bait not applicable to fishing rods
* Removed the C icon on rune items

# 1.0.1.0-beta
* Rune items now show the first letter of the stored rune
* Curse of corrosion now damages the armor
* Runes can now be disabled by adding them to the `runeenchanting:runeenchanting/runes/disabled` tag

# 1.0.0.2-beta
* Added a `runeenchanting:weapons` item tag

# 1.0.0.1-beta
* Added `get_random_rune` sub command
* Fixed enchantment replacement removing the enchantment component breaking anvil repair
  * Added `fix_enchantments_component` sub command to restore the enchantments component on items in main hand broken by the above bug

# 1.0.0-beta
* First Release