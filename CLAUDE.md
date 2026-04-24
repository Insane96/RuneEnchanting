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

`data/enhancement/Rune.java` — abstract base class for all runes. Each rune has a `ResourceLocation` ID and a priority. The three override points are:

- `onMiningSpeed(MiningContext)` → returns a modified `float` speed
- `onAttack(AttackContext)` → returns modified `float` damage
- `onLivingTick(TickContext)` → side-effect hook, no return value

Default implementations are no-ops (pass-through). `Efficiency.java` is the only concrete rune: it returns `originalSpeed * 2 + 2.5`.

### Context Classes

Thin data holders passed to rune methods:

- `MiningContext` — `miner`, `Optional<BlockPos> pos`, `level`, `originalSpeed`, `newSpeed`
- `AttackContext` — `attacker`, `attacked`, `damage`, `damageSource`, `isCritical`
- `TickContext` — `ticker` (the `LivingEntity` being ticked)

### Current Development State

Infrastructure is in place but incomplete:
- `RuneFeature` creates `MiningContext` but doesn't yet pass it through any runes or write the result back to the event
- `onAttack` and `onLivingTick` event listeners are not yet registered
- No rune registry exists yet
- `setup/REItemComponents.java` is an empty placeholder
- `assets/runeenchanting/lang/en_us.json` is empty
- No mixins are registered (config is ready)

Don't write code unless prompted or confirmed to do.
Other mod's source, is in C:\Users\delvi\source\repos\Insane96\
If you need Minecraft/Neo code ask instead of going into a rabbit hole try to read it, so I can provide it.