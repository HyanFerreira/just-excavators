package net.hfstack.justexcavators.recipe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.Test;

final class SilkCoreApplicationPolicyTest {
	@Test
	void acceptsAnUnenhancedExcavatorWithoutConflictingEnchantments() {
		assertTrue(SilkCoreApplicationPolicy.canApply(ExcavatorEnhancements.EMPTY, false, false));
	}

	@Test
	void rejectsAnExistingSilkEnhancementInEitherSlot() {
		assertFalse(SilkCoreApplicationPolicy.canApply(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK),
				false,
				false
		));
		assertFalse(SilkCoreApplicationPolicy.canApply(
				ExcavatorEnhancements.EMPTY.withSlot(1, EnhancementType.SILK),
				false,
				false
		));
	}

	@Test
	void rejectsNativeSilkTouch() {
		assertFalse(SilkCoreApplicationPolicy.canApply(ExcavatorEnhancements.EMPTY, true, false));
	}

	@Test
	void rejectsFortune() {
		assertFalse(SilkCoreApplicationPolicy.canApply(ExcavatorEnhancements.EMPTY, false, true));
	}

	@Test
	void acceptsSilkBesideCompatibleFilterOrSmeltingCore() {
		assertTrue(SilkCoreApplicationPolicy.canApply(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.FILTER),
				false,
				false
		));
		assertTrue(SilkCoreApplicationPolicy.canApply(
				ExcavatorEnhancements.EMPTY.withSlot(1, EnhancementType.SMELTING),
				false,
				false
		));
	}

	@Test
	void rejectsResultingSilkAndFortuneConflict() {
		assertFalse(SilkCoreApplicationPolicy.canApply(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.FILTER),
				false,
				true
		));
	}

	@Test
	void rejectsAFullExcavatorEvenWhenExistingPairIsCompatible() {
		ExcavatorEnhancements full = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.COLLECTOR)
				.withSlot(1, EnhancementType.FILTER);
		assertFalse(SilkCoreApplicationPolicy.canApply(full, false, false));
	}
}
