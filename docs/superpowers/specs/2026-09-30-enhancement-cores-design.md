# Enhancement Cores and Enhancement Workbench Design

**Status:** Implemented; automated verification passed, interactive playtests pending

**Date:** 2026-09-30

**Project:** JustExcavators

**Target:** Fabric, Minecraft 26.3

## Purpose

Make Enhancement Cores a modular progression system for Excavators. Each
Excavator keeps one excavation mode and may contain up to two compatible
Enhancement Cores. Players install, remove, and replace those Cores through a
dedicated Enhancement Workbench instead of the Smithing Table.

The system must preserve vanilla block-breaking behavior, loot-table and
datapack compatibility, multiplayer authority, and existing Excavators that
already contain the legacy Silk Core component.

## Scope

This design covers:

- the two-slot Enhancement Core data model;
- migration from the existing boolean Silk Core component;
- the Enhancement Workbench block, menu, screen, and slot behavior;
- Silk, Collector, Smelting, Filter, and Void Core behavior;
- Core and enchantment compatibility;
- block-breaking, loot, XP, inventory, and durability rules;
- localization, functional visual assets, and automated verification.

The following are explicitly outside this implementation:

- the help button represented by `!` in the concept image;
- external inventories, backpacks, pipes, or storage-mod integration;
- fuel consumption by Smelting Core;
- tag-family filtering;
- final survival recipes and balance costs for the Workbench and four new
  Cores. Until those recipes receive a separate content-design decision, the
  new items and block are available through Creative mode and commands. The
  existing Silk Core crafting recipe remains available.

## User-facing names

| Identifier | `en_us` | `pt_br` |
|---|---|---|
| Enhancement Workbench | Enhancement Workbench | Mesa de Trabalho de Aprimoramento |
| Silk Core | Silk Core | Núcleo de Seda |
| Collector Core | Collector Core | Núcleo Coletor |
| Smelting Core | Smelting Core | Núcleo de Fundição |
| Filter Core | Filter Core | Núcleo de Filtro |
| Void Core | Void Core | Núcleo do Vazio |

Existing translations may be retained where they already establish a
different project convention, but the Workbench name above is authoritative.

## Domain model

### Enhancement type

`EnhancementType` contains exactly these values:

```text
SILK
COLLECTOR
SMELTING
FILTER
VOID
```

There is no `NONE` value. Absence is represented by an empty slot.

### Installed enhancements

`ExcavatorEnhancements` is an immutable pair of optional fixed positions named
`slot_1` and `slot_2`. Either position may be independently empty, allowing a
player to install the first Core in either visible Workbench slot. Slot identity
must remain stable across persistence, tooltip rendering, networking, and menu
interaction; removing slot 1 must not move slot 2 automatically.

Every construction or mutation operation enforces these invariants:

- exactly two addressable positions, each empty or containing one Core;
- no duplicate type;
- every pair must be compatible;
- invalid serialized data is rejected rather than silently normalized.

A central compatibility policy is the only authority for pair and enchantment
validation. Recipes, slots, tooltips, tests, and the excavation pipeline must
not duplicate the matrix independently.

### Compatibility matrix

Allowed pairs:

| First | Second |
|---|---|
| Silk | Collector |
| Silk | Smelting |
| Silk | Filter |
| Collector | Smelting |
| Collector | Filter |
| Smelting | Filter |
| Filter | Void |

Forbidden pairs:

- Silk + Void;
- Collector + Void;
- Smelting + Void;
- two instances of the same Core.

Void therefore combines only with Filter.

### Enchantment compatibility

- Silk Core cannot coexist with the Silk Touch enchantment because the two are
  redundant.
- Silk Core cannot coexist with Fortune.
- Smelting Core may coexist with Fortune.
- Smelting Core may coexist with enchanted Silk Touch.
- Void Core cannot coexist with Fortune or Silk Touch.
- Efficiency, Unbreaking, Mending, and unrelated enchantments are unaffected.

Compatibility is checked in both directions: when a Core is installed and when
an enchantment is applied to a tool that already contains Cores. Administrative
commands may still construct invalid stacks. If an invalid Core/enchantment
combination reaches block breaking, all Enhancement Core effects are disabled
for that break and the real enchantments keep their vanilla behavior. This
fallback must never duplicate drops.

### Legacy migration

The persistent codec accepts both formats:

1. the legacy boolean, where `true` becomes `[SILK]` and `false` becomes `[]`;
2. the new fixed-slot representation.

New writes always use the fixed-slot representation. The network codec uses only the
new representation after the persistent value has been decoded. Existing
Excavators retain their name, damage, enchantments, excavation mode, and Silk
Core state.
When a legacy `true` value is decoded, it carries a transient migration marker so
the old synthetic Silk Touch enchantment is removed once without affecting a
new, administratively-created Silk Core/Silk Touch conflict. The marker is then
canonicalized and is never written to the new fixed-slot format.

## Enhancement Workbench

### Block lifecycle

The Workbench is a stateless workstation. It uses a server-side menu with
`ContainerLevelAccess` and does not use a persistent Block Entity inventory.
Breaking the block cannot drop menu contents because the block stores none.

Only a player close enough to the original Workbench may continue using the
menu. Removing the block or leaving interaction range closes the menu through
the normal validity check.

### Slot layout

```text
             [ Excavator: slot 0 ]

 [ Enhancement: slot 1 ] [ Enhancement: slot 2 ]

        Player inventory (3 x 9)
             Player hotbar
```

Slot 0 accepts exactly one Excavator. Slots 1 and 2 expose the corresponding
fixed component positions and accept exactly one registered Enhancement Core.
They remain disabled while slot 0 is empty.

The Excavator component is authoritative. Enhancement slots are server-owned
editable projections, not an independent item store. Displaying an installed
Core must not create a second recoverable copy.

### Loading a tool

When an Excavator enters slot 0, the server loads its fixed enhancement slots into
slots 1 and 2. Empty component positions produce empty slots. A non-Excavator
cannot enter slot 0.

The current tool must be finalized before slot 0 can be replaced by another
Excavator. The new tool is loaded only after the previous transaction succeeds.

### Installing and removing

Placing a Core into an empty enhancement slot consumes one Core item and adds
its type to that exact component position. Removing an installed Core removes
the component entry and creates one corresponding Core item on the cursor or in
the destination inventory.

Every operation is validated and committed on the logical server. Client-side
rendering never predicts a successful incompatible mutation.

### Direct replacement

When the cursor carries a Core and the player clicks an occupied enhancement
slot, the menu evaluates the prospective pair while keeping the other slot
unchanged.

If valid:

1. one carried Core becomes installed in the selected slot;
2. when the cursor held exactly one Core, the replaced Core becomes the carried
   item;
3. when the cursor held a stack, one new Core is consumed and the replaced Core
   moves to player inventory; the operation is rejected if that item cannot fit;
4. the tool component and visible slots are synchronized in the same operation.

If invalid, neither cursor nor tool changes. There is no automatic choice of a
slot to replace.

### Shift-click behavior

The menu implements `quickMoveStack` explicitly:

- an Excavator in player inventory moves to slot 0 if it is empty;
- a Core moves to the first compatible empty enhancement slot, checking slot 1
  before slot 2;
- an installed Core moves to player inventory if space exists;
- a tool in slot 0 moves to player inventory if space exists;
- with both enhancement slots occupied, Shift-click never replaces a Core;
- unrelated items move only between main inventory and hotbar;
- failed transfers leave source and destination unchanged.

### Closing and interruption

On normal close, distance invalidation, death, disconnect, or block removal:

- the latest valid component state remains on the Excavator;
- the Excavator is returned to player inventory;
- any real input or carried item is returned to player inventory;
- an item that cannot fit is dropped at the player's position;
- projected installed-Core slots are cleared without spawning copies.

Server authority and atomic mutation are required to prevent loss and
duplication during rapid clicks or disconnects.

## Screen and visual asset

The client screen extends the handled/container screen class for the registered
menu. It renders a flat GUI texture, dynamic item stacks, translated labels,
and vanilla tooltips.

The expected resource path is:

```text
assets/justexcavators/textures/gui/container/enhancement_workbench.png
```

The GUI texture contains only the panel, decorations, and slot frames. It must
not contain the world background, player hand, baked item icons, quantities,
tooltips, or localized text. Title and inventory labels are rendered by code.

The runtime GUI uses a vanilla-sized 256 x 256 texture atlas whose visible
panel occupies the top-left 176 x 166 pixels. It is rendered one-to-one, uses
the Crafting Table grayscale palette and 18 x 18 slot treatment for the player
inventory, hotbar, and functional slots, and leaves the unused atlas area
transparent. Dynamic items, quantities, tooltips, and translated labels remain
code-rendered. The world block uses its own 16 x 16 top, front, back, side, and
bottom textures through a dedicated cube model.

## Excavation processing

### Scope of effects

- Silk, Collector, Smelting, and Void affect the central block and every valid
  additional AOE block.
- Filter affects only additional AOE blocks.
- Sneaking disables only AOE. Other installed Core effects remain active for
  the central 1 x 1 break.

### Processing order

For each affected block, the server performs the following conceptual stages:

1. capture the original tool, installed Cores, central block state, and hit
   context;
2. validate permissions, protection, tool suitability, and target eligibility;
3. calculate additional positions;
4. apply Filter to additional positions;
5. build the effective loot context, including Silk Touch or Fortune;
6. let the block loot table calculate drops;
7. transform those drops through Smelting or discard them through Void;
8. insert results through Collector or spawn them at the broken block;
9. emit only the XP allowed by the active Core rules;
10. apply tool durability and stop safely when the tool can no longer continue.

Silk participates in loot calculation; it is not a transformation applied after
normal drops have already been calculated.

The implementation must preserve vanilla destruction callbacks, statistics,
advancements, protection checks, block-entity handling, and mod interoperability
as far as the available Fabric and Minecraft hooks permit. It must not replace
the entire vanilla break lifecycle with a simplified block removal routine.

## Core behavior

### Silk Core

Silk uses vanilla Silk Touch loot semantics without storing a real Silk Touch
enchantment on the Excavator. A temporary effective tool or equivalent loot
context is used only while calculating drops.

The existing `inventoryTick` behavior that restores a persistent Silk Touch
enchantment and the special Grindstone workaround are removed. A migration-only
tick remains to strip the old synthetic enchantment once from legacy boolean
stacks; it never adds or maintains an enchantment.

### Collector Core

Every resulting drop is inserted into the player inventory using vanilla stack
limits and merge behavior. Any remainder is spawned at the position of the
block that produced it. Collector does not collect XP and never deletes an item
because the inventory is full.

### Smelting Core

Smelting operates on the drops produced by the loot table, one input unit at a
time, using only normal furnace smelting recipes.

- Fortune changes loot before Smelting.
- Silk changes loot before Smelting.
- a matching recipe replaces each input unit with the recipe's assembled
  result, preserving result counts and components;
- an input with no smelting recipe remains unchanged;
- blasting, smoking, and campfire recipes are ignored;
- recipe XP is not generated;
- original block XP remains unchanged;
- a block costs extra durability only if at least one drop was transformed.

Examples:

```text
Clay Block + Silk + Smelting -> 1 Terracotta
Clay Block + Smelting -> 4 Clay Balls -> 4 Bricks
Sand + Smelting -> 1 Glass
Dirt + Smelting -> original Dirt drop
```

Recipe lookup uses the current server recipe access so datapack reloads are
respected. No global cache is introduced until profiling demonstrates a need.

### Filter Core

Filter compares the block identity of each additional AOE target with the
central block identity. It does not require every `BlockState` property to be
equal. The central block is always processed if it is otherwise eligible.

### Void Core

Void removes affected blocks without item drops or block XP. It remains active
while sneaking. Its tooltip includes a prominent destructive warning.

## Durability

- a normally processed block has the existing one-point base cost;
- a block for which Smelting transforms at least one drop has a two-point total
  potential cost;
- the cost is per block, not per transformed item;
- the normal enchantment pipeline applies Unbreaking independently to the
  potential damage;
- Creative players do not consume durability;
- processing stops before an additional AOE action can use a broken tool;
- the central vanilla break is allowed to break the tool normally.

## Tooltips and feedback

An Excavator tooltip lists enhancements in fixed slot order:

```text
Enhancement Cores (2/2):
- Silk Core
- Smelting Core
```

The count reflects zero, one, or two installed Cores. Invalid insertion and
replacement are rejected without consuming or moving items. The initial version
does not add a custom error dialog; slot refusal and tooltips provide feedback.

Void includes a localized warning equivalent to:

```text
WARNING: Destroyed blocks will not drop items or experience.
```

## Registration and source boundaries

Common/server source contains:

- block, block item, and menu registration;
- enhancement domain model and compatibility policy;
- Workbench menu transactions;
- excavation and loot processing;
- recipes, tags, and data generation.

Client source contains:

- Workbench screen registration and rendering;
- client-only screen classes.

No rendering class may be referenced from the common initialization path, so a
dedicated server can load the mod safely.

## Verification

### Verification status (2026-09-30)

The automated gate covers compatibility, fixed-slot persistence and cleanup of
the legacy synthetic Silk Touch enchantment, Workbench
transactions, central/additional scope, ordered loot transformation, item-count
conservation, XP suppression, durability decisions, tooltips, build, mixin
loading, and idempotent data generation. Common source is checked for client
imports, and the removed Smithing/Grindstone transition symbols are absent.

Dedicated-server startup and shutdown were verified with JustExcavators and
JustHammers loaded and no warnings or errors. Interactive multiplayer component
synchronization, claims/protection-mod, gravity-block, and datapack-reload
playtests remain external release checks. Final distinct artwork for the four
new Cores and undecided Survival recipes are content follow-ups, not missing
functional implementation.

### Unit tests

- all allowed and forbidden Core pairs;
- duplicate and third-Core rejection;
- ordered add, remove, and replace operations;
- legacy boolean codec migration and new codec round trips;
- enchantment compatibility;
- Filter block-identity comparison;
- Smelting transformation, unchanged fallback, and durability decision;
- quick-move routing decisions.

### Menu and integration tests

- loading zero, one, and two installed Cores;
- direct installation, removal, and replacement;
- incompatible replacement leaves all stacks unchanged;
- Shift-click prioritization and full-inventory failure;
- closing, disconnecting, moving out of range, and removing the block;
- no Core duplication across any menu lifecycle path;
- preservation of damage, name, enchantments, and excavation mode.

### Gameplay tests

- Silk loot for Grass Block, Podzol, Mycelium, and custom loot tables;
- Collector with empty, partial, and full inventory;
- Clay Block with and without Silk plus Smelting;
- Sand, non-smeltable drops, Fortune, and datapack-provided recipes;
- Filter across horizontal and vertical AOE orientations;
- Void on central, AOE, and sneaking breaks;
- XP and durability rules;
- multiplayer server authority and dedicated-server startup.

## Acceptance criteria

The feature is acceptable when:

1. every Excavator stores and synchronizes two fixed optional Core slots containing
   zero to two valid Cores;
2. legacy Silk Excavators load without data loss;
3. the Workbench supports safe installation, removal, direct replacement, and
   Shift-click behavior without duplication;
4. the five Cores produce the specified central and AOE behavior;
5. loot tables, current smelting recipes, inventory overflow, XP, and
   durability follow this document;
6. client-only rendering does not break dedicated-server loading;
7. automated tests cover domain rules and high-risk menu and gameplay flows;
8. the old Smithing Table Core-application path is removed without removing the
   standalone Silk Core crafting recipe.
