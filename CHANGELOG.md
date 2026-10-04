# Changelog

All notable changes to Just Excavators are documented in this file.

## [1.0.0] - 2026-10-03

### Release target

- Fabric release for Minecraft 1.20.1.
- Includes the complete feature set of the original release, including the excavation-area preview.

### Added

- Stone, Copper, Iron, Golden, Diamond, and Netherite Excavators.
- Basic (`3x3x1`), Deep (`3x3x3`), Wide (`5x5x1`), and Advanced (`5x5x3`) excavation profiles.
- Face-oriented, server-authoritative area mining for floors, ceilings, and walls.
- Deep, Wide, and Advanced Excavation Cores with their own crafting progression.
- Enhancement Workbench with two fixed Core slots and an in-game compatibility panel.
- Silk, Collector, Smelting, Filter, and Void Enhancement Cores.
- Survival recipes for the Workbench, Excavators, Excavation Cores, Core Housing, and Enhancement Cores.
- Diamond-to-Netherite upgrades that preserve the tool profile, durability, name, enchantments, and installed Cores.
- Dedicated advancement branches for excavation progression and Enhancement Core progression.
- English and Brazilian Portuguese translations, localized tooltips, generated recipes, models, tags, and block loot.
- A built-in optional resource pack that restores the vanilla-style Enhancement Workbench interface.

### Gameplay

- Holding sneak disables area mining and breaks only the selected block.
- Only shovel-mineable blocks are considered for additional area targets.
- Protected, unbreakable, excluded, fluid-containing, and block-entity targets are skipped safely.
- Every successful block break uses the normal Minecraft lifecycle for loot, XP, statistics, permissions, and durability.
- Unbreaking, Mending, Efficiency, Fortune, and Silk Touch interactions follow vanilla behavior.
- Collector overflow drops at the original block position instead of being deleted.
- Smelting uses currently loaded furnace recipes, including compatible datapack and modded recipes.
- Datapacks can exclude blocks through the `justexcavators:excavator_no_aoe` block tag.

### Compatibility

- Requires Minecraft 1.20.1, Fabric Loader 0.16.14 or newer, Fabric API, and Java 17 or newer.
- Must be installed on both the client and server.
- Runs independently without requiring JustHammers.
