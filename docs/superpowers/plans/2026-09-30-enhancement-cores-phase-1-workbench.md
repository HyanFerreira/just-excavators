# Enhancement Cores Phase 1 — Workbench Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a playable, server-authoritative Enhancement Workbench that safely installs, removes, replaces, and Shift-moves two compatible Enhancement Cores on an Excavator.

**Architecture:** The Workbench is a stateless `Block` that opens an `AbstractContainerMenu` through `ContainerLevelAccess`; it has one real temporary Excavator slot and two server-owned projections of the Excavator component. A pure transaction policy decides every Core mutation before the menu changes cursor, inventory, projection, or component state, keeping compatibility and item conservation testable without a running client.

**Tech Stack:** Java 25, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Minecraft 26.3 Mojang mappings, JUnit 5, Gradle/Loom data generation.

**Spec:** `docs/superpowers/specs/2026-09-30-enhancement-cores-design.md`

## Global Constraints

- Continue directly on `main`, as explicitly authorized by the user.
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

## Review Focus

- A stacked-cursor replacement with a full player inventory must reject without consuming the incoming Core or changing the tool; pin in Task 2 transaction tests.
- Duplicate, incompatible, Silk Touch-conflicting, and Fortune-conflicting insertions must reject identically for direct click and Shift-click; pin in Task 2 policy tests.
- Closing after loading a two-Core Excavator must return only the real Excavator, never two projected Core copies; pin the projection/closure contract in Task 3 tests.
- Removing slot 1 while slot 2 is occupied must leave slot 2 fixed and visible; pin in Tasks 2 and 3 tests.
- Number-key swap, throw, drag, clone, and pickup-all must not bypass the Core transaction policy; pin supported-input routing in Task 2 and enforce it in Task 3.
- The transitional Silk Touch enchantment must not prevent adding a compatible second Core, and removing or replacing Silk must not leave its synthetic enchantment behind; pin policy behavior in Task 2 and component/enchantment reconciliation in Task 3.

---

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

- [ ] **Step 7: Perform a final self-review**

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
