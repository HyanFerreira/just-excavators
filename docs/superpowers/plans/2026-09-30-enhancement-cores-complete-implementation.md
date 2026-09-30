# Enhancement Cores — Complete Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete Enhancement Cores from the Workbench through Silk, Filter, Collector, Void, Smelting, presentation, and integration verification.

**Architecture:** The Workbench is a stateless, server-authoritative editor for the two fixed Core slots. Block breaking retains the vanilla `ServerPlayerGameMode.destroyBlock` lifecycle while scoped enhancement context supplies an effective Silk loot tool and intercepts only drop/XP delivery; focused policies implement Filter, Collector, Void, Smelting, and durability without replacing permission checks, callbacks, statistics, or loot tables.

**Tech Stack:** Java 25, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Minecraft 26.3 Mojang mappings, JUnit 5, Gradle/Loom data generation.

**Spec:** `docs/superpowers/specs/2026-09-30-enhancement-cores-design.md`

## Global Constraints

- Continue directly on `main`, as explicitly authorized by the user.
- Execute every remaining phase continuously from this plan without stopping for per-phase approval, as explicitly requested by the user.
- The Excavator `ENHANCEMENTS` component is the sole persistent authority; the block has no Block Entity or persistent inventory.
- Preserve damage, custom name, enchantments, excavation mode, and fixed `slot_1`/`slot_2` identity.
- All mutation is server-authoritative and must conserve physical Core items across success, rejection, close, disconnect, and invalidation.
- Shift-click may fill the first compatible empty Core slot, slot 1 before slot 2, but never replaces an occupied Core.
- Direct replacement affects only the clicked slot and is rejected atomically when incompatible or when a stacked cursor cannot store the outgoing Core.
- Core/enchantment compatibility continues to come from `EnhancementCompatibility`.
- Register the four new Core items for Creative/commands now so every Workbench path is playable; recipes and final distinct artwork remain Phase 5 scope.
- Use the committed 1213×1296 Workbench PNG as the runtime GUI source, rendered proportionally at 243×259; do not bake dynamic labels or items into it and do not modify it in this phase.
- Use a vanilla crafting-table model as the Workbench block/item placeholder until the user supplies the block artwork; do not invent final block art or a survival recipe.
- Common initialization must not reference client rendering classes.
- The `!` help button, Core effects, native Silk loot handling, and removal of the transitional Smithing/Grindstone path remain outside Phase 1.
- Until Phase 2 replaces persistent Silk Touch with a loot-context effect, a Silk Core's transitional Silk Touch enchantment is treated as synthetic by Workbench validation and is removed when that Silk Core leaves the tool.
- Normal furnace recipes are the only Smelting source; blasting, smoking, campfire, fuel use, and furnace recipe XP are excluded.
- Silk, Collector, Smelting, and Void affect central and AOE blocks; Filter affects only additional AOE blocks; sneaking disables only AOE.
- A missing final block texture and undecided survival recipes do not block functionality: retain the documented placeholder model and Creative/command availability until the user supplies art and balance recipes.

## Review Focus

- A stacked-cursor replacement with a full player inventory must reject without consuming the incoming Core or changing the tool; pin in Task 2 transaction tests.
- Duplicate, incompatible, Silk Touch-conflicting, and Fortune-conflicting insertions must reject identically for direct click and Shift-click; pin in Task 2 policy tests.
- Closing after loading a two-Core Excavator must return only the real Excavator, never two projected Core copies; pin the projection/closure contract in Task 3 tests.
- Removing slot 1 while slot 2 is occupied must leave slot 2 fixed and visible; pin in Tasks 2 and 3 tests.
- Number-key swap, throw, drag, clone, and pickup-all must not bypass the Core transaction policy; pin supported-input routing in Task 2 and enforce it in Task 3.
- The transitional Silk Touch enchantment must not prevent adding a compatible second Core, and removing or replacing Silk must not leave its synthetic enchantment behind; pin policy behavior in Task 2 and component/enchantment reconciliation in Task 3.

---

## Phase 1 — Mesa de Trabalho de Aprimoramento

### Task 1: Register the Enhancement Core item catalog

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/EnhancementCoreCatalog.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ModItems.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ModCreativeTab.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModItemModelProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModEnglishLanguageProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModPortugueseLanguageProvider.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/EnhancementCoreCatalogTest.java`
- Regenerate: `src/main/generated/assets/justexcavators/items/*.json`
- Regenerate: `src/main/generated/assets/justexcavators/models/item/*.json`
- Regenerate: `src/main/generated/assets/justexcavators/lang/en_us.json`
- Regenerate: `src/main/generated/assets/justexcavators/lang/pt_br.json`

**Interfaces:**
- Consumes: `EnhancementType` and the existing registered `ModItems.SILK_CORE`.
- Produces: `EnhancementCoreCatalog.typeOf(ItemStack) -> Optional<EnhancementType>`, `typeOf(Item) -> Optional<EnhancementType>`, `itemOf(EnhancementType) -> Item`, and `stackOf(EnhancementType) -> ItemStack`; registered `COLLECTOR_CORE`, `SMELTING_CORE`, `FILTER_CORE`, and `VOID_CORE` constants.

- [ ] **Step 1: Write the catalog tests**

  Assert that all five types round-trip through `itemOf`/`typeOf`, an empty stack and every excavation-mode Core return empty, and `stackOf` returns exactly one item.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.EnhancementCoreCatalogTest`

  Expected: compilation fails because `EnhancementCoreCatalog` and the four new item constants do not exist.

- [ ] **Step 3: Implement registration and the immutable bidirectional catalog**

  Add the four items to `ModItems.CORES` after `SILK_CORE`. Use item identity, reject non-Core items, and fail fast if a type is missing or mapped twice.

- [ ] **Step 4: Add provisional presentation data**

  Add the approved English and Brazilian Portuguese names. Generate the four provisional item models against the existing `silk_core` texture so they render now without claiming final Phase 5 art.

- [ ] **Step 5: Verify tests and generated data**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.EnhancementCoreCatalogTest runDatagen`

  Expected: PASS; four item definitions/models and eight translation entries are generated, with no recipes for the new Cores.

- [ ] **Step 6: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/enhancement/EnhancementCoreCatalog.java \
    src/main/java/net/hfstack/justexcavators/item/ModItems.java \
    src/main/java/net/hfstack/justexcavators/item/ModCreativeTab.java \
    src/client/java/net/hfstack/justexcavators/datagen/ModItemModelProvider.java \
    src/client/java/net/hfstack/justexcavators/datagen/ModEnglishLanguageProvider.java \
    src/client/java/net/hfstack/justexcavators/datagen/ModPortugueseLanguageProvider.java \
    src/test/java/net/hfstack/justexcavators/enhancement/EnhancementCoreCatalogTest.java \
    src/main/generated/assets/justexcavators
  git commit -m "feat(cores): register enhancement core catalog"
  ```

### Task 2: Define atomic Workbench transactions

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchTransactions.java`
- Test: `src/test/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchTransactionsTest.java`

**Interfaces:**
- Consumes: `ExcavatorEnhancements`, `EnhancementType`, and `EnhancementCompatibility.areEnchantmentsCompatible(...)`.
- Produces: `click(ExcavatorEnhancements, int slot, Optional<EnhancementType> carriedType, int carriedCount, boolean canStoreOutgoing, boolean hasSilkTouch, boolean hasFortune) -> Optional<CoreClickResult>`; `firstQuickInstallSlot(ExcavatorEnhancements, EnhancementType, boolean hasSilkTouch, boolean hasFortune) -> OptionalInt`; `acceptsCoreInput(ContainerInput) -> boolean`.
- Produces: immutable `CoreClickResult(ExcavatorEnhancements enhancements, int consumedFromCursor, Optional<EnhancementType> cursorReturn, Optional<EnhancementType> inventoryReturn)`.

- [ ] **Step 1: Write failing click-transaction tests**

  Cover installation into either exact empty slot, removal to an empty cursor, one-item direct replacement to the cursor, stacked direct replacement to inventory, full-inventory rejection, incompatible/duplicate replacement, invalid slot indexes, and preservation of a gapped second slot.

- [ ] **Step 2: Write failing Shift-click and input-mode tests**

  Assert slot-1-first selection, fallback to slot 2 when slot 1 is incompatible but slot 2 is valid, rejection when full, no implicit replacement, enchantment conflicts, and acceptance only of normal pickup for direct Core-slot interaction; `QUICK_MOVE` is handled separately by the menu. Also assert that Silk Touch is treated as synthetic when the current component already contains Silk, while native Silk Touch on a tool without Silk still rejects Silk/Void.

- [ ] **Step 3: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.workbench.EnhancementWorkbenchTransactionsTest`

  Expected: compilation fails because the transaction policy does not exist.

- [ ] **Step 4: Implement the minimal pure transaction policy**

  Treat rejection as `Optional.empty()`. A cursor count of zero means removal; an empty target consumes one Core without a return; a one-item replacement returns the old Core to the cursor; a stacked replacement requires `canStoreOutgoing` and returns the old Core to inventory. Validate the resulting fixed slots and enchantments before constructing a result.

- [ ] **Step 5: Run focused and full tests**

  Run: `./gradlew test --tests net.hfstack.justexcavators.workbench.EnhancementWorkbenchTransactionsTest && ./gradlew test`

  Expected: PASS.

- [ ] **Step 6: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchTransactions.java \
    src/test/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchTransactionsTest.java
  git commit -m "feat(workbench): define atomic core transactions"
  ```

### Task 3: Implement the server-side Workbench block and menu

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/block/EnhancementWorkbenchBlock.java`
- Create: `src/main/java/net/hfstack/justexcavators/block/ModBlocks.java`
- Create: `src/main/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchMenu.java`
- Create: `src/main/java/net/hfstack/justexcavators/workbench/ModMenuTypes.java`
- Modify: `src/main/java/net/hfstack/justexcavators/JustExcavators.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ModItems.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ModCreativeTab.java`
- Test: `src/test/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchProjectionTest.java`

**Interfaces:**
- Consumes: Task 1 catalog; Task 2 transaction policy; `ExcavatorComponents.ENHANCEMENTS`.
- Produces: `ModBlocks.ENHANCEMENT_WORKBENCH`, `ModItems.ENHANCEMENT_WORKBENCH`, `ModMenuTypes.ENHANCEMENT_WORKBENCH`, and public menu slot constants `TOOL_SLOT = 0`, `CORE_SLOT_1 = 1`, `CORE_SLOT_2 = 2`, `PLAYER_INVENTORY_START = 3`.
- Produces: constructors `EnhancementWorkbenchMenu(int, Inventory)` for the client and `EnhancementWorkbenchMenu(int, Inventory, ContainerLevelAccess)` for the server.

- [ ] **Step 1: Write failing projection-contract tests**

  Test the package-visible projection helper used by the menu: loading zero/one/two/gapped Core states yields exact catalog stacks; clearing projections for closure returns no physical Core stacks; applying a valid mutation changes only the tool component and refreshed projections while preserving every other stack component.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.workbench.EnhancementWorkbenchProjectionTest`

  Expected: compilation fails because the menu projection implementation does not exist.

- [ ] **Step 3: Register the stateless block, block item, and menu type**

  Copy crafting-table physical properties, set explicit registry keys on block/item properties, open a `SimpleMenuProvider` from `useWithoutItem`, and initialize blocks before items and menus from the common entrypoint. Do not add a Block Entity.

- [ ] **Step 4: Add all 39 menu slots at the approved logical coordinates**

  Tool `(114, 50)`; Core 1 `(74, 97)`; Core 2 `(154, 97)`; player inventory starts `(14, 147)` with 24-pixel column/row spacing; hotbar starts `(14, 223)` with 24-pixel spacing. Tool accepts exactly one `ExcavatorItem`; Core slots have max size one, are inactive without a tool, and accept only catalog Cores allowed by the prospective component/enchantment state.

- [ ] **Step 5: Implement authoritative direct-click mutations**

  Intercept Core-slot `PICKUP` and delegate to Task 2. Preflight complete insertion of an outgoing Core before stacked replacement, then commit cursor/inventory changes, the exact tool component, projections, and broadcast as one server operation. Reject unsupported Core-slot input modes without mutation.

- [ ] **Step 6: Implement explicit `quickMoveStack` routing**

  Route player Excavators to tool slot, player Cores to the first Task 2 target, installed Cores to player inventory only if the whole item fits, tool back to player inventory, and unrelated items between main inventory and hotbar. Never replace through Shift-click and never shrink a source until the destination has succeeded.

- [ ] **Step 7: Implement tool loading and lifecycle cleanup**

  Keep projections synchronized after every accepted mutation. While the Phase 0 bridge remains, ensure a newly installed Silk Core has Silk Touch, ignore that synthetic enchantment during later Workbench validation, and remove only that synthetic Silk Touch when Silk leaves the component; preserve every unrelated enchantment. When the tool changes, finalize the old tool before projecting the new one. `stillValid` must use `ContainerLevelAccess` and the exact Workbench block. `removed` must call the vanilla carried-item cleanup, return only the real tool through `clearContainer`, and clear projection storage without returning or dropping projected Cores.

- [ ] **Step 8: Verify focused tests, full tests, and common/client compilation**

  Run: `./gradlew test --tests net.hfstack.justexcavators.workbench.EnhancementWorkbenchProjectionTest && ./gradlew test compileJava compileClientJava`

  Expected: PASS with no common-source reference to `net.minecraft.client`.

- [ ] **Step 9: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/block \
    src/main/java/net/hfstack/justexcavators/workbench \
    src/main/java/net/hfstack/justexcavators/JustExcavators.java \
    src/main/java/net/hfstack/justexcavators/item/ModItems.java \
    src/main/java/net/hfstack/justexcavators/item/ModCreativeTab.java \
    src/test/java/net/hfstack/justexcavators/workbench/EnhancementWorkbenchProjectionTest.java
  git commit -m "feat(workbench): add server-authoritative workstation"
  ```

### Task 4: Add the Workbench screen, generated resources, and phase verification

**Files:**
- Create: `src/client/java/net/hfstack/justexcavators/client/EnhancementWorkbenchScreen.java`
- Modify: `src/client/java/net/hfstack/justexcavators/client/JustExcavatorsClient.java`
- Create: `src/client/java/net/hfstack/justexcavators/datagen/ModBlockLootTableProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/JustExcavatorsDataGenerator.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModItemModelProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModEnglishLanguageProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModPortugueseLanguageProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModBlockTagProvider.java`
- Create: `src/main/resources/assets/justexcavators/blockstates/enhancement_workbench.json`
- Regenerate: `src/main/generated/data/justexcavators/loot_table/blocks/enhancement_workbench.json`
- Regenerate: `src/main/generated/assets/justexcavators/items/enhancement_workbench.json`
- Regenerate: `src/main/generated/data/minecraft/tags/block/mineable/axe.json`
- Modify: `docs/superpowers/specs/2026-09-30-enhancement-cores-design.md`
- Modify: `ROADMAP.md`

**Interfaces:**
- Consumes: Task 3 menu/type/block and the existing GUI texture at `assets/justexcavators/textures/gui/container/enhancement_workbench.png`.
- Produces: client-only `EnhancementWorkbenchScreen(EnhancementWorkbenchMenu, Inventory, Component)` and client registration through `MenuScreens.register`.

- [ ] **Step 1: Implement the client screen against the existing image**

  Use the five-argument `AbstractContainerScreen` constructor with `243 × 259`. Blit the full 1213×1296 source into that destination with `RenderPipelines.GUI_TEXTURED`. Center the translated title inside the top banner and scale it down only when wider than 162 pixels; draw the translated inventory label at `(14, 134)`. Leave item stacks and tooltips to the superclass.

- [ ] **Step 2: Register the screen only from the client entrypoint**

  Register `ModMenuTypes.ENHANCEMENT_WORKBENCH` with `MenuScreens.register`; verify no client class is imported from common/server source.

- [ ] **Step 3: Add functional placeholder block resources and loot**

  Point the Workbench blockstate and block-item definition to the vanilla crafting-table model until user artwork arrives. Add a self-drop loot table and include the block in `minecraft:mineable/axe`. Add `container.justexcavators.enhancement_workbench` and `block.justexcavators.enhancement_workbench` translations using “Enhancement Workbench” and “Mesa de Trabalho de Aprimoramento”. Do not add a recipe.

- [ ] **Step 4: Regenerate and inspect all output**

  Run: `./gradlew runDatagen`

  Expected: BUILD SUCCESSFUL; generated Workbench loot/item data and all Core translations/models are stable on a second run.

- [ ] **Step 5: Update authoritative documentation**

  Correct the GUI section to state that the committed flat PNG is intentionally rendered scaled as the Phase 1 runtime texture and that only the block art is pending. Mark all Phase 1 roadmap checkboxes complete only after verification.

- [ ] **Step 6: Run complete verification**

  Run: `./gradlew test build runDatagen`

  Expected: BUILD SUCCESSFUL; the second datagen pass writes zero files; `git diff --check` passes; `git status --short` shows only intended files before commit.

- [ ] **Step 7: Perform the Phase 1 checkpoint review**

  Review the full Phase 1 diff for item conservation, client/common source separation, block-removal invalidation, unsupported click paths, exact fixed-slot preservation, and stale generated data. Independent subagent review is unavailable unless the session’s no-delegation restriction changes.

- [ ] **Step 8: Commit**

  ```bash
  git add src/client/java/net/hfstack/justexcavators \
    src/main/resources/assets/justexcavators/blockstates/enhancement_workbench.json \
    src/main/generated \
    docs/superpowers/specs/2026-09-30-enhancement-cores-design.md \
    ROADMAP.md
  git commit -m "feat(workbench): add enhancement workbench screen"
  ```

## Phase 2 — Pipeline de loot e Silk nativo

### Task 5: Introduce scoped enhancement activation for every break

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/ActiveEnhancements.java`
- Create: `src/main/java/net/hfstack/justexcavators/excavation/ExcavationBreakContext.java`
- Modify: `src/main/java/net/hfstack/justexcavators/excavation/ExcavationHandler.java`
- Modify: `src/main/java/net/hfstack/justexcavators/mixin/ServerPlayerGameModeMixin.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/ActiveEnhancementsTest.java`
- Test: `src/test/java/net/hfstack/justexcavators/excavation/ExcavationBreakContextTest.java`

**Interfaces:**
- Consumes: fixed slots from Phase 0 and the Workbench output from Tasks 1–4.
- Produces: `ActiveEnhancements.resolve(ExcavatorEnhancements, boolean hasSilkTouch, boolean hasFortune) -> ActiveEnhancements`, `has(EnhancementType)`, and `valid()`; scoped `ExcavationBreakContext.open(ServerPlayer, ItemStack originalTool, ActiveEnhancements, BlockPos origin, BlockState centralState, boolean additional) -> Scope`; `current() -> Optional<ExcavationBreakContext>`.

- [ ] **Step 1: Write failing activation tests**

  Assert that valid installed pairs remain active, empty slots are valid and inactive, and an invalid Core/enchantment combination disables every Core for that break without changing the tool component.

- [ ] **Step 2: Write failing scoped-context tests**

  Assert current-context visibility inside a scope, restoration after close, correct nested-scope restoration, and no leakage when the scoped action throws.

- [ ] **Step 3: Run the focused tests and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.ActiveEnhancementsTest --tests net.hfstack.justexcavators.excavation.ExcavationBreakContextTest`

  Expected: compilation fails because both types are absent.

- [ ] **Step 4: Implement activation and a stack-disciplined ThreadLocal context**

  Store only immutable break facts in the context. `Scope.close()` must require LIFO ownership and always remove the ThreadLocal when the stack becomes empty.

- [ ] **Step 5: Scope central and additional vanilla destruction**

  Resolve the original tool once at central-break preparation. Open a central scope around vanilla `Block.playerDestroy`; for each additional target, open an `additional=true` scope around the existing `gameMode.destroyBlock(target)` call. Keep the existing recursion guard and permission/target validation.

- [ ] **Step 6: Run focused and full tests**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.ActiveEnhancementsTest --tests net.hfstack.justexcavators.excavation.ExcavationBreakContextTest && ./gradlew test`

  Expected: PASS.

- [ ] **Step 7: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/enhancement/ActiveEnhancements.java \
    src/main/java/net/hfstack/justexcavators/excavation/ExcavationBreakContext.java \
    src/main/java/net/hfstack/justexcavators/excavation/ExcavationHandler.java \
    src/main/java/net/hfstack/justexcavators/mixin/ServerPlayerGameModeMixin.java \
    src/test/java/net/hfstack/justexcavators/enhancement/ActiveEnhancementsTest.java \
    src/test/java/net/hfstack/justexcavators/excavation/ExcavationBreakContextTest.java
  git commit -m "refactor(cores): scope enhancement break context"
  ```

### Task 6: Replace persistent Silk Touch with native loot-context Silk

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/SilkLootTool.java`
- Create: `src/main/java/net/hfstack/justexcavators/mixin/BlockDropMixin.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ExcavatorItem.java`
- Modify: `src/main/java/net/hfstack/justexcavators/recipe/ModRecipeSerializers.java`
- Modify: `src/main/resources/justexcavators.mixins.json`
- Delete: `src/main/java/net/hfstack/justexcavators/enhancement/SilkCoreGrindstoneBehavior.java`
- Delete: `src/main/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicy.java`
- Delete: `src/main/java/net/hfstack/justexcavators/recipe/SilkCoreSmithingRecipe.java`
- Delete: `src/main/java/net/hfstack/justexcavators/mixin/GrindstoneMenuMixin.java`
- Delete: `src/main/java/net/hfstack/justexcavators/mixin/GrindstoneResultSlotMixin.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModRecipeProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModAdvancementProvider.java`
- Delete generated: `src/main/generated/data/justexcavators/recipe/silk_core_smithing.json`
- Delete generated: `src/main/generated/data/justexcavators/advancement/recipes/tools/silk_core_smithing.json`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/SilkLootToolTest.java`
- Delete test: `src/test/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicyTest.java`

**Interfaces:**
- Consumes: Task 5 active context.
- Produces: `SilkLootTool.forLoot(ItemStack, ActiveEnhancements, Holder<Enchantment> silkTouch) -> ItemStack`, returning the original stack when Silk is inactive and a count-one component-preserving copy with temporary Silk Touch when active.

- [ ] **Step 1: Write failing effective-tool tests**

  Using a bootstrapped enchantment registry, assert inactive identity, active copy isolation, Silk Touch only on the copy, and preservation of damage, custom name, excavation mode, other enchantments, and enhancement slots.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.SilkLootToolTest`

  Expected: compilation fails because `SilkLootTool` is absent.

- [ ] **Step 3: Implement the effective loot tool and drop hook**

  Wrap the `Block.playerDestroy` invocation from `ServerPlayerGameMode.destroyBlock` so vanilla statistics, exhaustion, removal, callbacks, and `ItemStack.mineBlock` remain untouched. Substitute only the tool argument used by `Block.dropResources` while the Task 5 context is active.

- [ ] **Step 4: Remove every persistent-Silk bridge**

  Remove `inventoryTick` enchant restoration, Grindstone mixins/helpers, the custom Smithing recipe/serializer and its generated recipe/advancement. Preserve the standalone Silk Core crafting recipe and all existing migrated component data.

- [ ] **Step 5: Regenerate and verify**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.SilkLootToolTest runDatagen && ./gradlew test build`

  Expected: PASS; no `silk_core_smithing` output or Grindstone mixin remains; the standalone `silk_core` recipe remains.

- [ ] **Step 6: Commit**

  ```bash
  git add -A src/main/java src/client/java src/main/resources/justexcavators.mixins.json src/main/generated src/test/java
  git commit -m "feat(silk): use native loot context without enchant persistence"
  ```

## Phase 3 — Filter, Collector e Void

### Task 7: Apply Filter before additional AOE destruction

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/FilterCorePolicy.java`
- Modify: `src/main/java/net/hfstack/justexcavators/excavation/ExcavationHandler.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/FilterCorePolicyTest.java`

**Interfaces:**
- Consumes: Task 5 `ActiveEnhancements` and central/additional `BlockState` facts.
- Produces: `FilterCorePolicy.shouldProcess(boolean additional, ActiveEnhancements, Block centralBlock, Block candidateBlock) -> boolean`.

- [ ] **Step 1: Write failing Filter tests**

  Assert the central block always passes, Filter accepts an additional state of the same `Block` despite different state properties, rejects a different block, and inactive/invalid Filter does not restrict AOE.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.FilterCorePolicyTest`

  Expected: compilation fails because `FilterCorePolicy` is absent.

- [ ] **Step 3: Implement and integrate before `destroyBlock`**

  Compare block identity, not full state equality. Skip rejected additional positions before any durability, drops, callbacks, or world mutation occurs.

- [ ] **Step 4: Run focused and full tests**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.FilterCorePolicyTest && ./gradlew test`

  Expected: PASS.

- [ ] **Step 5: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/enhancement/FilterCorePolicy.java \
    src/main/java/net/hfstack/justexcavators/excavation/ExcavationHandler.java \
    src/test/java/net/hfstack/justexcavators/enhancement/FilterCorePolicyTest.java
  git commit -m "feat(filter): restrict additional excavation targets"
  ```

### Task 8: Deliver Collector and Void drops and XP safely

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/EnhancementDropDelivery.java`
- Modify: `src/main/java/net/hfstack/justexcavators/mixin/BlockDropMixin.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/EnhancementDropDeliveryTest.java`

**Interfaces:**
- Consumes: Task 5 scoped active context and vanilla-produced `ItemStack` drops.
- Produces: `EnhancementDropDelivery.deliver(ItemStack, boolean collector, boolean voiding, Function<ItemStack, ItemStack> inventoryInsert, Consumer<ItemStack> worldDrop) -> DeliveryResult`; `DeliveryResult(insertedCount, droppedCount, discardedCount)`.

- [ ] **Step 1: Write failing delivery tests**

  Cover normal world drop, full Collector insertion, partial insertion with exact remainder at the originating block, full-inventory fallback, Void discarding without either destination, empty stacks, and count conservation for every non-Void path.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.EnhancementDropDeliveryTest`

  Expected: compilation fails because the delivery policy is absent.

- [ ] **Step 3: Implement delivery and wrap vanilla resource emission**

  Intercept only the per-stack resource emission inside vanilla `Block.dropResources`. Void short-circuits item emission and `BlockState.spawnAfterBreak` so it suppresses original block XP; Collector inserts with vanilla inventory behavior and passes an exact remainder to vanilla `Block.popResource` at the source position.

- [ ] **Step 4: Verify central, AOE, and sneaking context routing**

  Add cases proving Collector/Void are active for central and additional contexts and remain active for the central block while sneaking prevents only the AOE loop.

- [ ] **Step 5: Run focused and full tests**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.EnhancementDropDeliveryTest && ./gradlew test build`

  Expected: PASS.

- [ ] **Step 6: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/enhancement/EnhancementDropDelivery.java \
    src/main/java/net/hfstack/justexcavators/mixin/BlockDropMixin.java \
    src/test/java/net/hfstack/justexcavators/enhancement/EnhancementDropDeliveryTest.java
  git commit -m "feat(cores): add collector and void drop delivery"
  ```

## Phase 4 — Smelting

### Task 9: Transform furnace drops and apply Smelting durability

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/SmeltingDropProcessor.java`
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/SmeltingRecipeResolver.java`
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/EnhancementDurability.java`
- Modify: `src/main/java/net/hfstack/justexcavators/excavation/ExcavationBreakContext.java`
- Modify: `src/main/java/net/hfstack/justexcavators/mixin/BlockDropMixin.java`
- Modify: `src/main/java/net/hfstack/justexcavators/mixin/ServerPlayerGameModeMixin.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/SmeltingDropProcessorTest.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/EnhancementDurabilityTest.java`

**Interfaces:**
- Consumes: vanilla loot after Silk/Fortune and before Task 8 delivery.
- Produces: `SmeltingRecipeResolver.smeltOne(ServerLevel, ItemStack) -> Optional<ItemStack>` using only `RecipeType.SMELTING`; `SmeltingDropProcessor.process(ItemStack, Function<ItemStack, Optional<ItemStack>>) -> SmeltingResult`; `SmeltingResult(List<ItemStack> outputs, boolean transformed)`; `EnhancementDurability.extraPotentialDamage(boolean smeltingActive, boolean transformedAnyDrop) -> int`.

- [ ] **Step 1: Write failing unit transformation tests**

  With literal resolver fixtures, cover one sand to one glass, four clay balls to four bricks, a multi-count recipe output multiplied per input unit, component-preserving recipe results, no-recipe unchanged fallback, mixed transform/fallback, and empty input.

- [ ] **Step 2: Write failing durability tests**

  Assert zero extra damage for inactive Smelting, no drops, or no transformed drop; exactly one extra potential point for a block with any number of transformed units; and Creative bypass.

- [ ] **Step 3: Run focused tests and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.SmeltingDropProcessorTest --tests net.hfstack.justexcavators.enhancement.EnhancementDurabilityTest`

  Expected: compilation fails because the processor and durability policy are absent.

- [ ] **Step 4: Implement normal-furnace recipe lookup and per-unit multiplication**

  Query the current server `RecipeManager` for `RecipeType.SMELTING` with a one-item input on every lookup; do not cache across datapack reloads. Do not consult blasting, smoking, or campfire recipes and do not award recipe XP.

- [ ] **Step 5: Compose Smelting before Collector/Void delivery**

  Let Void discard without recipe work. Otherwise transform each vanilla loot stack, feed every result through Task 8, and mark the current break context transformed if at least one unit matched.

- [ ] **Step 6: Apply one extra potential damage point after the block lifecycle**

  After vanilla `playerDestroy` completes, use the normal item damage/enchantment path so Unbreaking independently evaluates the extra point. Never damage Creative players; stop subsequent AOE work once the original tool is broken or replaced.

- [ ] **Step 7: Run focused and full tests**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.SmeltingDropProcessorTest --tests net.hfstack.justexcavators.enhancement.EnhancementDurabilityTest && ./gradlew test build`

  Expected: PASS.

- [ ] **Step 8: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/enhancement \
    src/main/java/net/hfstack/justexcavators/excavation/ExcavationBreakContext.java \
    src/main/java/net/hfstack/justexcavators/mixin \
    src/test/java/net/hfstack/justexcavators/enhancement
  git commit -m "feat(smelting): process drops with furnace recipes"
  ```

## Phase 5 — Conteúdo funcional e apresentação

### Task 10: Finish Core tooltips and functional presentation

**Files:**
- Modify: `src/main/java/net/hfstack/justexcavators/item/ExcavatorItem.java`
- Create: `src/main/java/net/hfstack/justexcavators/item/EnhancementCoreItem.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ModItems.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModEnglishLanguageProvider.java`
- Modify: `src/client/java/net/hfstack/justexcavators/datagen/ModPortugueseLanguageProvider.java`
- Test: `src/test/java/net/hfstack/justexcavators/item/EnhancementTooltipContentTest.java`

**Interfaces:**
- Consumes: all five implemented `EnhancementType` values and fixed slot order.
- Produces: `EnhancementType.translationKey()`, `descriptionTranslationKey()`, and optional `warningTranslationKey()` for Void; `EnhancementTooltipContent.lines(ExcavatorEnhancements) -> List<Component>`.

- [ ] **Step 1: Write failing tooltip-content tests**

  Assert localized-key structure, `Enhancement Cores (0/2)` through `(2/2)`, fixed slot order including a slot-2-only tool, descriptions for every Core, and the destructive Void warning key.

- [ ] **Step 2: Run the focused test and verify RED**

  Run: `./gradlew test --tests net.hfstack.justexcavators.item.EnhancementTooltipContentTest`

  Expected: compilation fails because tooltip content APIs are absent.

- [ ] **Step 3: Implement item and Excavator tooltips**

  Replace the Silk-only Excavator line with the ordered summary. Use a dedicated `EnhancementCoreItem` to show each Core's behavior and the red Void warning. Keep all user-facing prose in `en_us` and `pt_br` translation providers.

- [ ] **Step 4: Regenerate and verify presentation data**

  Run: `./gradlew test --tests net.hfstack.justexcavators.item.EnhancementTooltipContentTest runDatagen && ./gradlew test build`

  Expected: PASS and stable generated language/model output. Final distinct Core/block art and survival recipes remain explicitly deferred external content, with functional placeholders retained.

- [ ] **Step 5: Commit**

  ```bash
  git add src/main/java/net/hfstack/justexcavators/item \
    src/main/java/net/hfstack/justexcavators/enhancement/EnhancementType.java \
    src/client/java/net/hfstack/justexcavators/datagen \
    src/main/generated/assets/justexcavators \
    src/test/java/net/hfstack/justexcavators/item/EnhancementTooltipContentTest.java
  git commit -m "feat(cores): add enhancement tooltip presentation"
  ```

## Phase 6 — Integração e verificação

### Task 11: Integration verification, documentation, and release gate

**Files:**
- Modify: `src/test/java/net/hfstack/justexcavators/excavation/ExcavationExecutionPolicyTest.java`
- Create: `src/test/java/net/hfstack/justexcavators/enhancement/EnhancementPipelineTest.java`
- Modify: `JUSTEXCAVATORS_ENHANCEMENT_CORES.md`
- Modify: `docs/superpowers/specs/2026-09-30-enhancement-cores-design.md`
- Modify: `ROADMAP.md`

**Interfaces:**
- Consumes: Tasks 1–10 as one ordered pipeline.
- Produces: an executable regression matrix covering compatibility, scope, ordering, conservation, XP decisions, and durability; documentation matching shipped behavior.

- [ ] **Step 1: Write the cross-Core pipeline tests**

  Cover Silk→Smelting, Fortune loot→Smelting, Smelting→Collector, Filter→Void, invalid administrative combinations falling back to vanilla effects, central behavior while sneaking, partial inventory overflow, and per-block durability. Use literal fake loot/recipe/inventory boundaries while exercising the real policy composition.

- [ ] **Step 2: Run the integration tests and verify RED where coverage exposes missing composition**

  Run: `./gradlew test --tests net.hfstack.justexcavators.enhancement.EnhancementPipelineTest --tests net.hfstack.justexcavators.excavation.ExcavationExecutionPolicyTest`

  Expected: any missing composition case fails for its named behavior; after the minimal fixes, all focused tests pass.

- [ ] **Step 3: Run data and source-boundary checks**

  Run: `./gradlew runDatagen build`

  Expected: BUILD SUCCESSFUL; a second `runDatagen` writes zero files; common source contains no `net.minecraft.client` imports; Smithing/Grindstone transition symbols are absent.

- [ ] **Step 4: Update the authoritative documents**

  Mark phases 1–4 complete. Mark the functional parts of Phase 5 complete and explicitly retain final block/Core artwork plus undecided survival recipes as external-content follow-up. Record which Phase 6 checks are automated and which require an interactive multiplayer/playtest environment.

- [ ] **Step 5: Run the final full suite and inspect repository state**

  Run: `./gradlew test build runDatagen && git diff --check && git status --short`

  Expected: BUILD SUCCESSFUL, no generated drift, and only intended documentation/test changes before commit.

- [ ] **Step 6: Commit**

  ```bash
  git add src/test/java JUSTEXCAVATORS_ENHANCEMENT_CORES.md \
    docs/superpowers/specs/2026-09-30-enhancement-cores-design.md ROADMAP.md src/main/generated
  git commit -m "test(cores): verify enhancement pipeline"
  ```

- [ ] **Step 7: Perform the whole-plan review and one fix pass**

  Build the review package from commit `f1dac6f` through `HEAD`. Because subagent dispatch is prohibited in this session, apply the `requesting-code-review` rubric as an explicit self-review, ledger every ruling/deferred minor, fix all Critical/Important findings through RED→GREEN tests in one pass, and repeat the full suite.
