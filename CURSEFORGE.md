# Just Excavators

![Just Excavators Banner](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/just-excavators-banner.png)

[CurseForge](https://www.curseforge.com/members/thecyber27/projects) |
[GitHub](https://github.com/HyanFerreira/just-excavators) |
[Issues](https://github.com/HyanFerreira/just-excavators/issues)

---

## 📖 About

**Move more terrain without turning Minecraft into a one-click quarry.**

Just Excavators adds specialized area-mining shovels designed for predictable, vanilla-style terrain work. Choose a
profile for the shape you need, aim at the surface you want to excavate, and let the server process every valid block
through Minecraft's normal breaking flow.

The mod preserves vanilla loot, XP, durability, enchantments, permissions, and game-mode restrictions. Hold sneak at
any time to disable area mining and break only the block you selected.

---

## 📚 Features

### ⛏️ Four Excavation Profiles

![Basic Excavation Profile](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/basic-excavation-profile.png)

Each Excavator carries one of four specialized profiles:

| Profile | Maximum area | Best for |
|---|---:|---|
| **Basic** | `3x3x1` | Everyday terrain work |
| **Deep** | `3x3x3` | Digging into large volumes |
| **Wide** | `5x5x1` | Clearing and leveling surfaces |
| **Advanced** | `5x5x3` | High-volume excavation |

The face you hit determines the area's orientation. Deep profiles extend inward from that face, so floors, ceilings,
and walls behave consistently.

![Wide Excavation Profile](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/wide-excavation-profile.png)

---

### 🧱 Six Tool Materials

![Excavators and Enhancement Cores](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/excavators-and-cores.png)

Build Excavators from Stone, Copper, Iron, Gold, Diamond, and Netherite.

Basic, Deep, Wide, and Advanced profiles are stored directly on the tool. Upgrading a Diamond Excavator to Netherite
preserves its profile, durability state, custom name, enchantments, and installed Enhancement Cores.

---

### 🔧 Enhancement Workbench

![Enhancement Workbench](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/enhancement-workbench.png)

Every Excavator has two fixed Enhancement Core slots managed through the Enhancement Workbench.

- Install, remove, or replace Cores without recreating the tool.
- Invalid pairs and duplicate Cores are rejected.
- A built-in compatibility panel shows which combinations are allowed.
- All changes are server-authoritative.
- An optional built-in resource pack restores the vanilla-style Workbench interface.

---

### ✨ Enhancement Cores

Customize how blocks and drops are processed:

- **Silk Core:** uses vanilla Silk Touch loot behavior without permanently enchanting the tool.
- **Collector Core:** sends drops to your inventory and leaves overflow in the world.
- **Smelting Core:** processes drops through current furnace recipes.
- **Filter Core:** limits additional targets to the same block type as the center block.
- **Void Core:** destroys drops and block XP.

Compatible pairs include Silk with Collector, Smelting, or Filter; Collector with Smelting or Filter; Smelting with
Filter; and Void with Filter. Fortune remains incompatible with the Silk Core.

---

### 🛡️ Vanilla-Friendly Area Mining

![Area Mining in Action](https://raw.githubusercontent.com/HyanFerreira/just-excavators/refs/heads/main/docs/media/area-mining-in-action.png)

Just Excavators does not replace the normal block-breaking lifecycle.

- Only shovel-mineable blocks are included in the additional area.
- Protected, unbreakable, fluid-containing, excluded, and block-entity targets are skipped.
- Each successfully broken block consumes durability normally.
- Unbreaking, Mending, Efficiency, Fortune, loot tables, XP, and statistics continue to work through vanilla systems.
- Area mining starts only after the center block is successfully broken.
- Holding sneak always limits the action to a single block.

---

### 🏆 Progression and Advancements

Craft each profile directly, progress through Deep, Wide, and Advanced Excavation Cores, and upgrade Diamond tools to
Netherite at the Smithing Table. A separate advancement branch introduces the Enhancement Workbench and challenges
you to install compatible Core pairs and collect all five Enhancement Cores.

---

## 📦 Installation

➡️ **Required on client and server**

➡️ Requires **Minecraft 1.21.1**

➡️ Requires **Fabric Loader 0.19.5+**

➡️ Requires **Fabric API**

➡️ Requires **Java 25+**

JustHammers is supported as an optional compatibility scenario but is not required.

---

## 🧩 Notes

- Area mining is authoritative on the server.
- Datapacks can exclude blocks through the `justexcavators:excavator_no_aoe` block tag.
- Smelting uses the currently loaded furnace recipes, including compatible datapack and modded recipes.
- Collector overflow is dropped at the original block position instead of being deleted.
- Administratively created invalid Core combinations safely disable enhancement effects for that break.

---

## 🏆 Credits

Created by **Hyan Ferreira**.

Inspired by the area-tool gameplay of **JustHammers**, while remaining a standalone and independently implemented mod.
