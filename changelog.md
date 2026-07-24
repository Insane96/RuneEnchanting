# Upcoming
* Added "Damage reduction: " infos to all damage reduction runes
* Fixed healthy not stacking on multiple pieces

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