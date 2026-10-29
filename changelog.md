# Upcoming
Requires InsaneLib 2.4.36.0
* Configs have been split by side. **Options moved to the new files must be set again**
  * `main.toml` and `runes.toml` are now server configs: values are decided by the server and synced to clients, and can be overridden per world in `saves/<world>/serverconfig/`
  * The new `common.toml` contains "Integrated data pack"
  * The new `client.toml` contains "Hide enchanting related items", "Always show extra infos" and "Glitch curse names"
* Bows and crossbows now share all their runes: Infinity, Gravity Defying, Knockback and Flame can be applied to crossbows, Piercing, Multishot, Quick Charge and Curse of Slow Charge to bows
  * Quick Charge and Curse of Slow Charge are now a percentage of the charge time (-60% and +40%) instead of flat seconds, and also change how fast bows are drawn
* Melee weapon runes can now be applied to bows and crossbows too, and work on the arrows shot
  * Sharpness, Smite, Bane of Hissing, Bane of Noses, Impaling, Water Coolant, Fire Surge and Rage deal more bonus damage with arrows, configurable separately
  * Smite's Weakness, Bane of Hissing's Slowness and Water Coolant's freeze aren't applied by arrows, unless enabled in the config
  * Electrocution charges the bow or crossbow that shot the arrow, as long as it's still in the shooter's inventory
  * Explosive, Curse of Explosion and Part Breaker work like in melee
* Removed Power, merged into Sharpness. Items with Power now have Sharpness
* Sharpness now also applies to thrown tridents (requires the upcoming InsaneLib version)
* Reworked Electrocution
  * Instead of charging the target on every hit and discharging after a delay, damage dealt now charges the weapon. Once 20 damage has been accumulated, the hit discharges
  * The accumulated damage slowly decreases over time (2 per second)
  * The discharge damage is now dealt separately and ignores invulnerability frames
  * The discharge now hits the target and the nearest 4 entities (up from 2), but no longer chains further
  * The discharge no longer hits the attacker, its teammates, its pets and armor stands
* Items with more runes than sockets (e.g. after sockets were decreased) now have their runes inactive until removed with a grindstone. Curses still apply
* Decreased Leather armor sockets
* Increased Enduring Rune bonus durability (+100% -> +200%)
* Decreased Explosive Rune delay (5s -> 3s)
* Decreased Sprint Pact Rune bonus speed (20% -> 15%)
* Curse of Blood Pact and Blood-fueled damage now bypasses armor and no longer consumes its durability
* Fixed the bonus damage from vanilla enchantments (e.g. Sharpness) and from runes such as Power and Hunter being ignored for players, mobs and thrown tridents
* Fixed runes on weapons and tools working while held in the offhand (e.g. Atmospheric giving attack speed). Like vanilla enchantments, they now only work in the main hand
* Fixed arrows being duplicated when shooting with vanilla Multishot, in creative mode or with infinite arrows, as the extra arrows could be picked up. Now only the arrows actually consumed can be picked up
* Fixed Magic Protection reducing the duration of beneficial effects too, instead of only negative ones
* Fixed Curse of Blood Pact almost never hurting when triggered by armor, and Blood-fueled/Blood Pact damage being ignored right after taking damage
* Fixed Magic Protection, Projectile Protection, Soulbound, Double Jump and Curse of the Void still working when disabled via config or the `disabled` rune tag
* Fixed Veining and Tunneling breaking blocks in protected areas (spawn protection, claims, Adventure mode). Additional blocks are now broken like normal player mining, so other mods can prevent it
* Fixed Explosive destroying blocks even when `mobGriefing` is false or `destroyBlocks` is disabled in the config. The explosion is now attributed to the attacker, so kills count as theirs and mods can prevent the griefing
* Fixed Soulbound items being lost if the death was canceled by another mod (e.g. a modded totem)
* Fixed Telekinesis sending the whole inventory of killed players into the killer's inventory, bypassing grave mods
* Fixed Double Jump not working when walking off a ledge if, since the last double jump, you landed without jumping
* Fixed Veining and Tunneling continuing to mine blocks after the tool broke
* Fixed the bonus speed from Retreat, Atmospheric and Curse of Unstable Motion staying forever if the rune stopped working without unequipping the item (e.g. rune disabled, removed with a command or item with more runes than sockets)
* Fixed Recovery causing an equipment change every second while healing, which made the speed from Retreat, Atmospheric and Unstable Motion flicker. The damage to recover is now stored on the entity instead of the chestplate
* Swapping an item for another one with the same rune (e.g. Retreat) no longer resets its bonus for a moment
* Fixed disabled runes being applicable with an anvil, and items with only disabled runes losing them when merged in an anvil
* Fixed disabled curses still granting a bonus socket and counting towards the max curses
* Fixed items losing their runes with a grindstone no longer stacking with identical items
* Fixed `/rune get_random_rune` losing the rune when the inventory is full. It's now dropped instead
* Fixed loot generation breaking when every eligible rune tag in a rune loot modifier has a weight of 0
* Fixed Atmospheric tooltip showing the wrong durability bonus under rain (0.25% instead of 300%)
* Curse names in tooltips now randomly glitch, with characters getting obfuscated every now and then (can be disabled in the config)
* Translation keys for sockets, overloaded runes, unknown curses, cursed info and learned curses are now prefixed with `runeenchanting.` to avoid conflicts with other mods
* Fixed players losing all their experience levels on every login when `disable_experience` is false. Only the fake levels given while experience is disabled are now removed
* Fixed enchanted books in loot generating two runes instead of one in some loot tables (e.g. mineshafts and ancient cities) when `disable_experience` is false
* Fixed mobs with enchanted equipment rolling for runes again every time they were loaded (e.g. chunk reloads), when `disable_experience` is false or no rune was applicable. The roll now happens only once per mob
* Fixed experience orbs saved in the spawn chunks being deleted when loading a world with `disable_experience` set to false
* Fixed the client using the `disable_experience` value of the previously joined server until the new one was received
* Fixed items with vanilla enchantments losing their glint when all their runes were removed

# 1.3.3.0
* Items with runes can no longer be used as the right item in an anvil, preventing their runes from being lost when merging
* Items with only curses now have the enchantment glint
* Lapis-fueled can now use lapis lazuli inside shulker boxes and bundles in the inventory (disabled by default)

# 1.3.2.3
* Sprint Pact speed is now configurable
* Increased crossbow sockets
* Projectile Protection rune now uses InsaneLib's attribute modifier

# 1.3.2.2
* Removed 'C' icon on cursed items
* Fixed Tunneling not accounting for the mining direction correctly

# 1.3.2.1
* Fixed runes removed with a Grindstone always popping out from the top of the block instead of the side it's actually attached to (floor, ceiling, or the wall it's facing)

# 1.3.2.0
* Encased Rune got an actual texture
* Fixed Curse of Blood Pact damage not bypassing armor and enchantments
* Fixed atmospheric's bonus mining speed desynced client-server during night time

# 1.3.1.0-beta
* Added advancements for obtaining your first rune, removing a rune with a grindstone and learning a curse
* Veining and Tunneling are now applicable to shears

# 1.3.0.0-beta
* Added Electrocution (weapons)
  * Attacks charge the target; after a delay it discharges, damaging the target and chaining to nearby charged entities
* Added Blood-fueled (all durability items)
  * Chance to not consume durability, at the cost of a chance to hurt yourself with enchantment-bypassing damage
* Added Soulbound (all durability items)
  * The item is kept in your inventory when you die, instead of dropping
* Learned curses are now shared between players on the same scoreboard team
* Vibration Detection's glow is now only visible to the entity wearing the rune, instead of everyone
* Lowered Golden Items sockets (8 -> 6)
* Changed enchased rune recipe (7 lapis block -> 5 lapis block + 2 golden blocks)
* Fixed EMI integration breaking EMI
* Fixed sockets not correctly reset when removing curses
* Fixed curse of blood pact dealing knockback on damage
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