# Just Excavators 1.0.0

The first stable release of Just Excavators introduces specialized area-mining shovels built for predictable,
vanilla-friendly terrain work.

## Highlights

- Six tool materials: Stone, Copper, Iron, Gold, Diamond, and Netherite.
- Four excavation profiles: Basic (`3x3x1`), Deep (`3x3x3`), Wide (`5x5x1`), and Advanced (`5x5x3`).
- Face-oriented excavation that behaves consistently on floors, ceilings, and walls.
- Sneak to temporarily disable area mining and break a single block.
- Enhancement Workbench with two fixed Core slots and an in-game compatibility panel.
- Five Enhancement Cores: Silk, Collector, Smelting, Filter, and Void.
- Dedicated recipes and advancements for Excavators, profile progression, and Enhancement Cores.
- Diamond-to-Netherite upgrades preserve the tool's profile, durability, name, enchantments, and installed Cores.
- English and Brazilian Portuguese translations.
- Optional built-in resource pack for the vanilla-style Enhancement Workbench interface.

## Vanilla-Friendly Behavior

- Each block uses Minecraft's normal breaking flow for loot, XP, permissions, statistics, enchantments, and durability.
- Protected, unbreakable, excluded, fluid-containing, and block-entity targets are skipped safely.
- Only shovel-mineable blocks are included in the additional area.
- Datapacks can exclude blocks with the `justexcavators:excavator_no_aoe` block tag.

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Fabric API
- Java 25 or newer
- Installed on both client and server
