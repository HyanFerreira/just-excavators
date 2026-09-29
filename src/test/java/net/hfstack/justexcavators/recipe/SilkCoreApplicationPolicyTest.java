package net.hfstack.justexcavators.recipe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class SilkCoreApplicationPolicyTest {
	@Test
	void acceptsAnUnenhancedExcavatorWithoutConflictingEnchantments() {
		assertTrue(SilkCoreApplicationPolicy.canApply(false, false, false));
	}

	@Test
	void rejectsAnExistingSilkEnhancement() {
		assertFalse(SilkCoreApplicationPolicy.canApply(true, false, false));
	}

	@Test
	void rejectsNativeSilkTouch() {
		assertFalse(SilkCoreApplicationPolicy.canApply(false, true, false));
	}

	@Test
	void rejectsFortune() {
		assertFalse(SilkCoreApplicationPolicy.canApply(false, false, true));
	}
}
