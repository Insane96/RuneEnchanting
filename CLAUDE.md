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

`RuneFeature.java` extends InsaneLib's `Feature` class and is annotated `@LoadFeature(canBeDisabled = false)`. It registers NeoForge event listeners. Currently it listens to `PlayerEvent.BreakSpeed` and builds a `MiningContext` — but the rune application logic is not yet wired in.

### Rune System

`data/runes/Rune.java` — abstract base class for all runes. Each rune has a `ResourceLocation` ID and a priority. Override points:

- `onMiningSpeed(MiningContext)` → returns modified `float` speed (default: pass-through)
- `onAttack(AttackContext)` → returns modified `float` damage (default: pass-through)
- `onLivingTick(TickContext)` → side-effect hook, no return value (default: no-op)
- `addAttributeModifiers(ItemAttributeModifierEvent)` → add attribute modifiers to the item (default: no-op)

Also has `getApplicableToItemTag()` and supports `@Config`-annotated fields processed at registry init.

Concrete runes (both registered in `setup/RERunes.java`):
- `EfficiencyRune.java` — implements `addAttributeModifiers()` to add `MINING_EFFICIENCY`; has `@Config` fields `bonusMiningSpeed` and `bonusFlatMiningSpeed`
- `SharpnessRune.java` — implements `addAttributeModifiers()` to add `ATTACK_DAMAGE`; has `@Config` field `bonusDamage`

### Context Classes

Thin data holders passed to rune methods:

- `MiningContext` — `miner`, `Optional<BlockPos> pos`, `level`, `originalSpeed`, `newSpeed`
- `AttackContext` — `attacker`, `attacked`, `damage`, `damageSource`, `isCritical`
- `TickContext` — `ticker` (the `LivingEntity` being ticked)

### Registry (`setup/RERunes.java`)

Custom NeoForge registry with key `runeenchanting:runes`. Registers `EFFICIENCY` and `SHARPNESS` as `DeferredHolder`s. Also calls `registerConfigs()` which processes `@Config`-annotated fields on each rune for config generation.

### Item Components (`setup/REItemComponents.java`)

Registers two `DataComponentType`s:
- `RUNES` — `List<Holder<Rune>>`, codec via `RERunes.REGISTRY.holderByNameCodec()`
- `SOCKETS` — `Integer`

### Items (`setup/REItems.java`)

Registers `RUNE` — a basic `Item` with default properties.

### Data Generation

`datagen/REItemTagProvider.java` — generates `rune_appliable_to/<rune>` item tags. Efficiency targets `#minecraft:pickaxes/axes/shovels/hoes`; sharpness targets `#minecraft:swords/axes`.

### Mixins

`mixin/PlayerMixin.java` — `@Mixin(Player.class)` empty placeholder. Declared in `runeenchanting.mixins.json`.

### Current Development State

Infrastructure is mostly in place but some wiring is incomplete:
- `RuneFeature` creates `MiningContext` but doesn't yet pass it through runes or write the result back to the `BreakSpeed` event
- `RuneFeature` handles `ItemAttributeModifierEvent` (calls `addAttributeModifiers()`) and `ItemTooltipEvent` (calls `getName()`/`getDescription()`) correctly
- `onAttack` and `onLivingTick` listeners are not yet registered (PlayerMixin is empty)

Don't write code unless prompted or confirmed to do.
Other mod's source, is in C:\Users\delvi\source\repos\Insane96\
If you need Minecraft/Neo code ask instead of going into a rabbit hole try to read it, so I can provide it.