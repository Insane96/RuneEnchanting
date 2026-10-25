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
- **InsaneLib 2.4.36.0** — custom base library providing `Feature`, `ILModConfig`, and `ComponentTranslation` utilities. Source can be found at C:\Users\delvi\source\repos\Insane96\InsaneLib (branch `1.21.1`; `26.1.2` is a different Minecraft version). Since 2.4.34.0 `ILModConfig` supports `ModConfig.Type.SERVER` configs (synced to clients on login and re-synced via `ServerConfigSyncMessage` when the file changes) and automatically registers the config screen for every mod that creates an `ILModConfig`. When a mod has more than one module (e.g. one `ILModConfig` per config type), every `@LoadFeature` must specify `module`, otherwise the feature is not loaded.
- **Parchment mappings** (2024.11.17) — human-readable field/method names in decompiled code

## Architecture

### Entry Point

`RuneEnchanting.java` — the `@Mod` class. Initializes three InsaneLib `ILModConfig`s, one module each (ids in `MAIN_MODULE`/`COMMON_MODULE`/`CLIENT_MODULE`):
- `CONFIG` — SERVER, `config/runeenchanting/main.toml` (module `runeenchanting:main`): gameplay options, `RuneFeature` and `RuneHooks`. Synced to clients, can be overridden per world in `saves/<world>/serverconfig/`.
- `COMMON_CONFIG` — COMMON, `config/runeenchanting/common.toml`: `RuneFeatureCommon` (`integratedDataPack`). Must stay COMMON because the world's data packs are picked before SERVER configs are loaded.
- `CLIENT_CONFIG` — CLIENT, `config/runeenchanting/client.toml`: `RuneFeatureClient` (visual-only options: tooltips, creative tab).

Before joining a world (and on clients before the server sends them) SERVER config values are the field defaults. Provides static utilities: `location(String)` → `ResourceLocation`, `lang(String)` → translation key, `translatableLang(String)` → `Component`.

### Feature System

`RuneFeature.java` extends InsaneLib's `Feature` class (`@LoadFeature(module = RuneEnchanting.MAIN_MODULE, canBeDisabled = false)`). Handles NeoForge events: `ItemAttributeModifierEvent`, `GetEnchantmentLevelEvent`, `ItemTooltipEvent`, `RegisterCommandsEvent`, and a `disableExperience` GameRule that cancels XP orbs, XP bottles, the experience bar, and vanilla enchanting. Also exposes static dispatcher methods (e.g. `onGetMaxDamage`, `onEnchantmentDamage`, `tickEffects`, `modifyDamageProtection`, …) called from mixins. `getRunesByPriority` is the central helper that reads `REDataComponents.RUNES`, filters disabled runes, and sorts by priority.

### Rune System

`runes/Rune.java` — abstract base class for all runes. Each rune has a `ResourceLocation` ID and a priority. Provides many override points covering mining, damage, projectiles, durability, protection, experience, fishing, and per-tick effects. Supports `@Config`-annotated static fields and an `Enabled` toggle processed via `loadConfig`/`readConfig`. Static helpers `Rune.isCurse(holder)` (curses are identified by the `runeenchanting:curse` rune tag) and `Rune.isEnabled(holder)` (config toggle + `disabled` rune tag). There is no flag to exclude a rune from random generation: random pools (`addRandomRunes`, `createRandomRuneItem`, loot modifiers) can include curses. `removeEffects(LivingEntity)` is called periodically for runes no longer active on the entity, to clean up attribute modifiers.

Concrete runes are in `runes/` and registered in `setup/RERunes.java`. Curses are in the `runes/curse/` sub-package. Rune configs are written to `runeenchanting/runes.toml`. `MomentumRune` exists but is currently commented out in `RERunes`.

### Registry (`setup/RERunes.java`)

Custom NeoForge registry with key `runeenchanting:runes`. All rune `DeferredHolder`s are registered here. `registerConfigs()` processes `@Config`-annotated fields on each rune and writes to `runeenchanting/runes.toml`, a SERVER config registered manually (not through `ILModConfig`): its listener skips `ModConfigEvent.Unloading` (values can't be read once unloaded) and on `Reloading` re-syncs the file to clients with `ServerConfigSyncMessage.syncToAll`.

### Item Components (`setup/REDataComponents.java`)

Registers `RUNES` (`List<Holder<Rune>>`), `SOCKETS` (`Integer`, sockets granted by curses are added on the fly by `RuneHelper.getSockets`), `STORED_RUNE` (the rune held by a rune item) and per-rune state components (`STACKED_DAMAGE`, `ELECTROCUTION_CHARGE`, `ELECTROCUTION_LAST_CHARGED_AT`, `CHARGED_JUMP`, `FIRE_GUARDIAN_LAST_USED`). `STORED_DAMAGE` is no longer written (Recovery stores it on the entity) and is kept registered only to load old items. Items with more runes than sockets are "overloaded": their runes are inactive (curses still apply).

### Items (`setup/REItems.java`)

Registers `RUNE` (`RuneItem`, holds a rune in `STORED_RUNE`) and `ENCASED_RUNE` (`EncasedRuneItem`, smelted into a random rune).

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

- `RuneHelper.java` — `hasRune`, `addRune`, `removeRune`, `clearRunes`, `addRandomRunes`, `createRandomRuneItem` helpers that read/write `REDataComponents.RUNES` and manage `ENCHANTMENT_GLINT_OVERRIDE` (set to `true` with runes, removed — not set to `false` — without, so vanilla enchantments keep their glint). Also `breakExtraBlock`, used by Veining/Tunneling to break blocks like normal player mining.
- `RECommands.java` — command registration (registered via `RuneFeature.onRegisterCommands`). In root package.
- `RuneHooksClient.java` — client-side event hooks (sprint, rendering, input) in root package.

### Network

`network/NetworkHandler.java` + `network/message/ClientboundDisableExperienceMessage.java` — syncs the `disableExperience` GameRule value to clients on login and on change. The server reads the GameRule in `ServerAboutToStartEvent` (from `getWorldData()`, levels aren't loaded yet); the client resets it to `true` on logout. `network/message/ServerboundDoubleJumpMessage.java` — client→server message for the DoubleJump rune.

### Loot System

Global loot modifiers in `data/loot/`:
- `RuneLootModifier` — converts enchanted items/books in loot into rune-socketed items. Supports weighted rune tag pools.
- `RuneCatchAllLootModifier` — fallback modifier ensuring loot tables always have a chance to drop runes.
- `ItemToRuneLootModifier` — replaces a given item in loot (e.g. experience bottles) with random rune items.
- `VoidCurseLootModifier` — handles `CurseOfTheVoid` item deletion on death.
- `SetRandomRuneLootFunction` — loot function that applies a random rune to an item (used in data-driven loot tables, e.g. spawner drops).

### Enchantment-to-Rune Mapping

`data/EnchantmentToRuneReloadListener` — a `SimplePreparableReloadListener` that reads JSON files mapping vanilla enchantment `ResourceLocation`s to rune `ResourceLocation`s. Currently unused: `ENCHANTMENT_TO_RUNE` / `getRuneForEnchantment` are never read, the loot modifiers pick random runes instead.

### Attributes

`setup/REAttributes.java` — registers the `runeenchanting:off_ground_mining_speed` attribute (syncable, range 0–1, default 0.2) used by mining-related runes.

### Current Development State

The system is largely wired in. Curses are fully implemented. Spawners drop runes when broken (via `integrated_packs/enchantment_changes` loot table + `SetRandomRuneLootFunction`). `MomentumRune` is implemented but commented out in `RERunes`.

Don't write code unless prompted or confirmed to do.
Other mod's source, is in C:\Users\delvi\source\repos\Insane96\

## Minecraft/NeoForge Sources

The decompiled Java sources for Minecraft/NeoForge (1.21.1, `net.neoforged.moddev` plugin) are already extracted to `C:\Users\delvi\.gradle\mc-sources\1.21.1-neoforge\` (normal package layout, e.g. `net/minecraft/world/entity/LivingEntity.java`) — read directly from there with Read/Grep/Glob instead of asking the user.

If missing or needing regeneration, the source jar is in the NeoForm cache at `~/.gradle/caches/neoformruntime/intermediate_results/mergeWithSources_*_output.jar` (pick the most recent by date) — extract it with `unzip` into the folder above, discarding the `.class` files.