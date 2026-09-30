# Enhancement Cores Phase 0 Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the legacy Silk-only boolean with a tested two-slot Enhancement Core domain model, centralized compatibility rules, and backward-compatible persistence while preserving current Silk gameplay.

**Architecture:** Add a serialized `EnhancementType` enum and a pure compatibility policy, then model the two visible Workbench positions as fixed optional slots in `ExcavatorEnhancements`. Decode legacy booleans but always encode the new fixed-slot object; adapt the existing Silk recipe, tooltip, inventory tick, and Grindstone integration without yet implementing the Workbench or new Core effects.

**Tech Stack:** Java 25, Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, Minecraft 26.3 Mojang mappings, Data Components, Mojang `Codec`, JUnit 5.13.4, Gradle 9.x through the project wrapper.

**Spec:** `docs/superpowers/specs/2026-09-30-enhancement-cores-design.md`

## Global Constraints

- Target Fabric and Minecraft 26.3 with Java 25; add no dependency.
- The logical server remains authoritative; Phase 0 adds no client screen or networking packet.
- Preserve item name, damage, enchantments, excavation mode, and legacy Silk Core state.
- Model two fixed optional slots; removing slot 1 must not shift slot 2.
- Reject duplicate, over-capacity, and incompatible Core combinations centrally.
- Decode the old boolean component and the new fixed-slot component; encode only the new format.
- Preserve the current persistent Silk Touch and Grindstone behavior temporarily; native loot-context Silk belongs to a later phase.
- Do not modify or commit the user's untracked GUI concept image.

## Review Focus

- A legacy `true` component must decode to Silk in slot 1, while `false` decodes to both slots empty; Task 2 pins both cases.
- Removing slot 1 while slot 2 is occupied must preserve slot 2 exactly; Task 2 tests the gap explicitly.
- Duplicate or incompatible serialized slots must return a codec error rather than throw or normalize; Task 2 tests both classes.
- Null optionals or an invalid slot index must not corrupt component state; Task 2 tests constructor and index rejection.
- Phase 0 must not silently remove the current Silk Touch enchantment or break Grindstone protection before native Silk exists; Task 2 runs policy tests plus the full build and audits the retained call sites.

---

### Task 1: Enhancement types and centralized compatibility

**Files:**
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/EnhancementType.java`
- Create: `src/main/java/net/hfstack/justexcavators/enhancement/EnhancementCompatibility.java`
- Test: `src/test/java/net/hfstack/justexcavators/enhancement/EnhancementCompatibilityTest.java`

**Interfaces:**
- Produces: `EnhancementType.CODEC`, serializing `silk`, `collector`, `smelting`, `filter`, and `void`.
- Produces: `EnhancementCompatibility.isValidPair(EnhancementType, EnhancementType) -> boolean`.
- Produces: `EnhancementCompatibility.isValidSlots(Optional<EnhancementType>, Optional<EnhancementType>) -> boolean`.
- Produces: `EnhancementCompatibility.areEnchantmentsCompatible(Optional<EnhancementType>, Optional<EnhancementType>, boolean hasSilkTouch, boolean hasFortune) -> boolean`.

- [ ] **Step 1: Write the compatibility tests**

Create parameterized or table-driven tests asserting all seven allowed pairs, the three Void conflicts, duplicate rejection, empty/single-slot acceptance, and these enchantment cases:

```text
Silk + Silk Touch -> false
Silk + Fortune -> false
Smelting + Silk Touch -> true
Smelting + Fortune -> true
Void + Silk Touch -> false
Void + Fortune -> false
Filter + neither -> true
```

Also assert `EnhancementType.CODEC` decodes each lowercase identifier and rejects an unknown identifier.

- [ ] **Step 2: Run the new test and verify it fails**

Run:

```bash
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test --tests '*EnhancementCompatibilityTest'
```

Expected: compilation fails because `EnhancementType` and `EnhancementCompatibility` do not exist.

- [ ] **Step 3: Implement `EnhancementType`**

Define the five enum constants, a lowercase serialized name accessor, and a `Codec<EnhancementType>` that returns a descriptive `DataResult.error` for unknown identifiers. Do not add `NONE`.

- [ ] **Step 4: Implement `EnhancementCompatibility`**

Keep the class stateless and non-instantiable. Encode the approved pair matrix once; make pair order irrelevant. `isValidSlots` accepts either slot empty and delegates a two-present pair to `isValidPair`. Enchantment validation examines both optional slots and applies the exact Global Constraints rules.

- [ ] **Step 5: Run focused and full tests**

Run the focused command from Step 2, then:

```bash
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test
```

Expected: both commands report `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit Task 1**

```bash
git add src/main/java/net/hfstack/justexcavators/enhancement/EnhancementType.java src/main/java/net/hfstack/justexcavators/enhancement/EnhancementCompatibility.java src/test/java/net/hfstack/justexcavators/enhancement/EnhancementCompatibilityTest.java
git commit -m "feat(enhancements): define core compatibility"
```

### Task 2: Fixed-slot migration and transitional Silk integration

**Files:**
- Modify: `src/main/java/net/hfstack/justexcavators/component/ExcavatorEnhancements.java`
- Modify: `src/main/java/net/hfstack/justexcavators/component/ExcavatorComponents.java`
- Modify: `src/main/java/net/hfstack/justexcavators/item/ExcavatorItem.java`
- Modify: `src/main/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicy.java`
- Modify: `src/main/java/net/hfstack/justexcavators/recipe/SilkCoreSmithingRecipe.java`
- Modify: `src/main/java/net/hfstack/justexcavators/enhancement/SilkCoreGrindstoneBehavior.java`
- Test: `src/test/java/net/hfstack/justexcavators/component/ExcavatorEnhancementsTest.java`
- Modify: `src/test/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicyTest.java`

**Interfaces:**
- Consumes: `EnhancementType` and `EnhancementCompatibility` from Task 1.
- Produces: `ExcavatorEnhancements.EMPTY`.
- Produces: `ExcavatorEnhancements.CODEC`, reading legacy booleans and new `{slot_1, slot_2}` objects and writing only objects.
- Produces: `slot(int) -> Optional<EnhancementType>`, `has(EnhancementType) -> boolean`, `size() -> int`, `canSet(int, EnhancementType) -> boolean`, `withSlot(int, EnhancementType) -> ExcavatorEnhancements`, and `withoutSlot(int) -> ExcavatorEnhancements`.
- Preserves: `ExcavatorComponents.ENHANCEMENTS` component identifier and persistent/network registration.
- Preserves temporarily: actual Silk Touch application, inventory-tick restoration, Grindstone retention, and the Smithing Table Silk application path.
- Produces: a compiling codebase with no `silk()` or `withSilk()` calls.

- [ ] **Step 1: Write domain invariant tests**

Test these exact states and transitions:

```text
EMPTY -> slot 0 empty, slot 1 empty, size 0
withSlot(1, FILTER) -> slot 0 empty, slot 1 FILTER, size 1
withSlot(0, SILK), then withSlot(1, SMELTING) -> valid, size 2
withoutSlot(0) from [SILK, SMELTING] -> [empty, SMELTING]
duplicate SILK replacement -> rejected
VOID beside FILTER -> accepted
VOID beside COLLECTOR -> rejected
slot indexes -1 and 2 -> rejected
```

Use `assertThrows(IllegalArgumentException.class, ...)` for invalid mutations and `assertFalse(canSet(...))` before each rejected mutation.

- [ ] **Step 2: Write codec migration and round-trip tests**

Using `JsonOps.INSTANCE`, assert:

- decoding `true` yields `[SILK, empty]`;
- decoding `false` yields `[empty, empty]`;
- decoding `{}` yields both slots empty;
- decoding `{"slot_2":"filter"}` preserves an empty first slot;
- new-format encode/decode preserves `[SILK, SMELTING]`;
- encoding migrated legacy Silk produces an object containing `slot_1`, not a boolean;
- duplicate and incompatible two-slot objects return codec errors.

- [ ] **Step 3: Run the new test and verify it fails**

Run:

```bash
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test --tests '*ExcavatorEnhancementsTest'
```

Expected: compilation fails because the legacy boolean record lacks the fixed-slot API.

- [ ] **Step 4: Implement the fixed-slot value object**

Represent `slot_1` and `slot_2` with non-null `Optional<EnhancementType>` values. Validate construction through `EnhancementCompatibility.isValidSlots`. Slot index `0` maps to `slot_1`; index `1` maps to `slot_2`; every other index throws `IllegalArgumentException` with the rejected index in its message.

- [ ] **Step 5: Implement the dual-format codec**

Create a new map codec with optional fields `slot_1` and `slot_2`. Combine it with `Codec.BOOL` for decoding; map legacy `true` to Silk in slot 1 and `false` to `EMPTY`. The reverse mapping always selects the new map representation. Validate decoded slot pairs with `DataResult.error` before constructing the value object.

- [ ] **Step 6: Run the focused test to expose integration callers**

Run the command from Step 3.

Expected: the new domain implementation compiles in isolation, while the project compiler identifies every legacy `silk()`/`withSilk()` caller that the remaining Task 2 steps must migrate. Do not commit this build-breaking intermediate state.

#### Task 2 integration steps: adapt the existing Silk feature

- [ ] **Step 7: Extend the Silk application policy tests**

Change the policy input from a Silk boolean to `ExcavatorEnhancements`. Retain existing assertions and add:

```text
empty slots + no conflicting enchantment -> accepted
Silk in slot 1 -> rejected
Silk in slot 2 -> rejected
Filter only + no conflicting enchantment -> accepted
Filter + Fortune -> rejected because the proposed Silk would conflict
Smelting only + no conflicting enchantment -> accepted
```

The transitional Smithing recipe installs Silk into the first empty slot and rejects a full tool. This recipe remains only until the Workbench phase removes it.

- [ ] **Step 8: Run the focused policy test and verify it fails**

Run:

```bash
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test --tests '*SilkCoreApplicationPolicyTest'
```

Expected: compilation fails until the policy and callers use `EnhancementType.SILK` and the fixed-slot API.

- [ ] **Step 9: Update component defaults and Silk checks**

Replace `ExcavatorEnhancements.NONE` with `EMPTY`. Replace `silk()` with `has(EnhancementType.SILK)` in item ticks, tooltips, and Grindstone behavior. Keep the current visible Silk tooltip and real enchantment restoration unchanged in this phase.

- [ ] **Step 10: Centralize the transitional Smithing validation**

Change `SilkCoreApplicationPolicy.canApply` to accept the component plus `hasSilkTouch` and `hasFortune`. Require a free slot and delegate Core/enchantment rules to `EnhancementCompatibility`; do not reproduce the pair matrix in the recipe.

- [ ] **Step 11: Update Silk Smithing assembly**

Find the first empty fixed slot in index order, set it to `SILK`, preserve every other `ItemStack` component through `copyWithCount(1)`, and continue adding real Silk Touch temporarily. `assemble` must defensively return an unchanged/empty-safe result if invoked for an input that no longer matches rather than overwrite an occupied slot.

- [ ] **Step 12: Run Phase 0 verification**

Run:

```bash
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew build
rg -n '\.silk\(\)|withSilk\(|ExcavatorEnhancements\.NONE' src
```

Expected: both Gradle commands report `BUILD SUCCESSFUL`; `rg` returns no matches.

- [ ] **Step 13: Commit Task 2**

The fixed-slot model and all existing Silk callers form one atomic migration.

```bash
git add src/main/java/net/hfstack/justexcavators/component/ExcavatorEnhancements.java src/main/java/net/hfstack/justexcavators/component/ExcavatorComponents.java src/main/java/net/hfstack/justexcavators/item/ExcavatorItem.java src/main/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicy.java src/main/java/net/hfstack/justexcavators/recipe/SilkCoreSmithingRecipe.java src/main/java/net/hfstack/justexcavators/enhancement/SilkCoreGrindstoneBehavior.java src/test/java/net/hfstack/justexcavators/component/ExcavatorEnhancementsTest.java src/test/java/net/hfstack/justexcavators/recipe/SilkCoreApplicationPolicyTest.java
git commit -m "refactor(enhancements): migrate to fixed core slots"
```

### Task 3: Phase 0 documentation and final audit

**Files:**
- Modify: `JUSTEXCAVATORS_ENHANCEMENT_CORES.md`
- Modify: `ROADMAP.md`

**Interfaces:**
- Consumes: completed Phase 0 implementation and verification output.
- Produces: project documentation that names the Workbench as the application mechanism, permits Silk + Smelting, specifies fixed slots, and records the new foundation phase without rewriting the historical completed phases.

- [ ] **Step 1: Update the concept handoff document**

Replace the outdated Smithing/crafting-choice language with the approved Workbench behavior. Resolve the third-Core rejection/replacement contradiction: Shift-click rejects when full, while direct slot interaction explicitly replaces the selected Core. Update the matrix to allow Silk + Smelting and make Void compatible only with Filter. Correct processing order so Silk participates before loot calculation.

- [ ] **Step 2: Add the new enhancement-system phases to the roadmap**

Keep the historical `Fase 0 — Fundação do projeto` unchanged. Add a separately named Enhancement Cores roadmap whose Phase 0 is the model/compatibility/migration delivered here and whose later phases cover Workbench, native Silk/pipeline, remaining Core effects, content assets, and gameplay verification.

- [ ] **Step 3: Run documentation and repository checks**

Run:

```bash
git diff --check
rg -n 'Silk \+ Smelting.*proibid|Smelting Core é incompatível com Silk Touch|forma definitiva ainda deve ser definida' JUSTEXCAVATORS_ENHANCEMENT_CORES.md
env JAVA_HOME=/home/hyanferreira/.jdks/jdk-25.0.4.1+1 PATH=/home/hyanferreira/.jdks/jdk-25.0.4.1+1/bin:$PATH ./gradlew test
git status --short
```

Expected: no whitespace errors; the stale-rule search returns no matches; tests succeed; status shows only intended documentation changes plus the user's pre-existing untracked GUI image.

- [ ] **Step 4: Commit Task 3**

```bash
git add JUSTEXCAVATORS_ENHANCEMENT_CORES.md ROADMAP.md
git commit -m "docs(cores): align roadmap with fixed enhancement slots"
```

## Phase 0 Completion Gate

Phase 0 is complete only when:

- all tests and `build` pass under JDK 25;
- legacy booleans decode and new values encode as fixed slots;
- every approved Core and enchantment compatibility rule has a unit test;
- current Silk gameplay still works through its transitional path;
- no legacy boolean accessor remains;
- the GUI concept image remains untouched and uncommitted;
- the concept document and roadmap agree with the approved design.
