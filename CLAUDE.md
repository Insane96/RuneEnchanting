# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**RuneEnchanting** (mod ID: `runeenchanting`) is a NeoForge mod for Minecraft 1.21.1 that implements a rune-based enchanting system. Runes modify items through mining speed, damage, and per-tick effects, similar to vanilla enchantments.

## Build Commands

```bash
./gradlew build          # Build JAR (output: build/libs/)
./gradlew runClient      # Launch Minecraft client with mod loaded
./gradlew runServer      # Launch dedicated server (--nogui)
./gradlew runData        # Run data generators (output: src/generated/resources/)
./gradlew clean          # Clean build outputs
```

No dedicated test task exists yet — use `runGameTestServer` for game tests.

## Key Dependencies

- **NeoForge 21.1.227** — mod loader
- **InsaneLib 2.4.13.1** — custom base library providing `Feature`, `ILModConfig`, and `ComponentTranslation` utilities. Source can be found at C:\Users\delvi\source\repos\Insane96\InsaneLib
- **Parchment mappings** (2024.11.17) — human-readable field/method names in decompiled code

## Architecture

### Entry Point

`RuneEnchanting.java` — the `@Mod` class. Initializes InsaneLib's `ILModConfig` for a COMMON-tier config at `config/runeenchanting/common.toml`. Provides static utilities: `location(String)` → `ResourceLocation`, `lang(String)` → translation key, `translatableLang(String)` → `Component`.

### Feature System

`RuneFeature.java` extends InsaneLib's `Feature` class (`@LoadFeature(canBeDisabled = false)`). Handles NeoForge events: `ItemAttributeModifierEvent`, `GetEnchantmentLevelEvent`, `ItemTooltipEvent`, `RegisterCommandsEvent`, and a `disableExperience` GameRule that cancels XP orbs, XP bottles, the experience bar, and vanilla enchanting. Also exposes static dispatcher methods (e.g. `onGetMaxDamage`, `onEnchantmentDamage`, `tickEffects`, `modifyDamageProtection`, …) called from mixins. `getRunesByPriority` is the central helper that reads `REDataComponents.RUNES`, filters disabled runes, and sorts by priority.

### Rune System

`runes/Rune.java` — abstract base class for all runes. Each rune has a `ResourceLocation` ID and a priority. Provides many override points covering mining, damage, projectiles, durability, protection, experience, fishing, and per-tick effects. Supports `@Config`-annotated static fields and an `Enabled` toggle processed via `loadConfig`/`readConfig`. Has `isCurse()` and `canGenerateRandomly()` flags.

Concrete runes are in `runes/` and registered in `setup/RERunes.java`. Curses are in the `runes/curse/` sub-package. Rune configs are written to `runeenchanting/runes.toml`. `MomentumRune` exists but is currently commented out in `RERunes`.

### Registry (`setup/RERunes.java`)

Custom NeoForge registry with key `runeenchanting:runes`. All rune `DeferredHolder`s are registered here. `registerConfigs()` processes `@Config`-annotated fields on each rune and writes to `runeenchanting/runes.toml`.

### Item Components (`setup/REDataComponents.java`)

Registers two `DataComponentType`s: `RUNES` (`List<Holder<Rune>>`) and `SOCKETS` (`Integer`), both persistent and network-synchronized.

### Items (`setup/REItems.java`)

Registers `RUNE` — a basic `Item` with default properties.

### Data Generation

- `REItemTagProvider.java` — generates `rune_applicable_to/<rune>` item tags per rune.
- `REEntityTypeTagProvider.java` — generates entity type tags used by runes.
- `RELanguageProvider.java` — generates language entries for rune names/descriptions.
- `REEnchantmentTagProvider.java` — generates enchantment tags used by the loot/conversion system.
- `RERuneTagProvider.java` — generates rune tags.
- `REGlobalLootModifierProvider.java` — generates global loot modifier JSON files.

### Mixins

All declared in `runeenchanting.mixins.json`. Server-side:
- `PlayerMixin` — `@Mixin(Player.class)` wraps `getEnchantedDamage` in `Player.attack` to call `RuneFeature.onEnchantmentDamage`.
- `IItemExtensionMixin` — `@Mixin(IItemExtension.class)` hooks `getMaxDamage` → `RuneFeature.onGetMaxDamage`.
- `EnchantmentHelperMixin` — `@Mixin(EnchantmentHelper.class)` hooks virtually all `EnchantmentHelper` static methods (damage, protection, projectiles, durability, experience, fishing, etc.) to dispatch to `RuneFeature` static methods. Also cancels `enchantItem`/`enchantItemFromProvider` when `disableExperience` is active.
- `LivingEntityMixin` — hooks into `LivingEntity` for rune-driven living entity effects.
- `MobMixin` — hooks `Mob` for mob-specific rune interactions.
- `AbstractArrowMixin` — hooks arrows for projectile rune effects.
- `ProjectileWeaponItemMixin` — hooks crossbow/bow firing for projectile rune effects.
- `EnchantWithLevelsFunctionMixin` / `EnchantRandomlyFunctionMixin` — cancels loot-table enchant functions when `disableExperience` is active (replaced by rune loot modifiers).
- `FishingHookAccessor` / `MobEffectInstanceAccessor` — accessor mixins exposing private fields.

Client-side (in `mixin/client/`):
- `GuiGraphicsMixin` — suppresses the XP bar rendering when `disableExperience` is active.
- `LightTextureMixin` — client light texture hook (used by a rune effect).
- `LevelRendererAccessor` — accessor for level renderer internals.

### Utilities

- `RuneHelper.java` — `hasRune`, `addRune`, `removeRune`, `addRandomRunes`, `createRandomRuneItem` helpers that read/write `REDataComponents.RUNES` and manage `ENCHANTMENT_GLINT_OVERRIDE`.
- `RECommands.java` — command registration (registered via `RuneFeature.onRegisterCommands`). In root package.
- `RuneHooksClient.java` — client-side event hooks (sprint, rendering, input) in root package.

### Network

`network/NetworkHandler.java` + `network/message/ClientboundDisableExperienceMessage.java` — syncs the `disableExperience` GameRule value to clients on login and on change. `network/message/ServerboundDoubleJumpMessage.java` — client→server message for the DoubleJump rune.

### Loot System

Global loot modifiers in `data/loot/`:
- `RuneLootModifier` — converts enchanted items/books in loot into rune-socketed items. Supports weighted rune tag pools.
- `RuneCatchAllLootModifier` — fallback modifier ensuring loot tables always have a chance to drop runes.
- `ExperienceBottleToRuneLootModifier` — converts experience bottle drops into rune items.
- `VoidCurseLootModifier` — handles `CurseOfTheVoid` item deletion on death.
- `SetRandomRuneLootFunction` — loot function that applies a random rune to an item (used in data-driven loot tables, e.g. spawner drops).

### Enchantment-to-Rune Mapping

`data/EnchantmentToRuneReloadListener` — a `SimplePreparableReloadListener` that reads JSON files mapping vanilla enchantment `ResourceLocation`s to rune `ResourceLocation`s. Used to translate enchanted loot into rune equivalents.

### Attributes

`setup/REAttributes.java` — registers the `runeenchanting:off_ground_mining_speed` attribute (syncable, range 0–1, default 0.2) used by mining-related runes.

### Current Development State

The system is largely wired in. Curses are fully implemented. Spawners drop runes when broken (via `integrated_packs/enchantment_changes` loot table + `SetRandomRuneLootFunction`). `MomentumRune` is implemented but commented out in `RERunes`.

Don't write code unless prompted or confirmed to do.
Other mod's source, is in C:\Users\delvi\source\repos\Insane96\
If you need Minecraft/Neo code ask, instead of going into a rabbit hole try to read it, so I can provide it.