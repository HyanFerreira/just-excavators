package net.hfstack.justexcavators.excavation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.excavation.ExcavationTargetValidator.TargetFacts;

final class ExcavationTargetValidatorTest {
	@Test
	void acceptsOnlySafeShovelTargets() {
		assertTrue(ExcavationTargetValidator.isEligible(new TargetFacts(
				true,
				true,
				true,
				true,
				false,
				false,
				false
		)));
	}

	@Test
	void rejectsEveryUnsafeTargetCondition() {
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(false, true, true, true, false, false, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, false, true, true, false, false, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, true, false, true, false, false, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, true, true, false, false, false, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, true, true, true, true, false, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, true, true, true, false, true, false)));
		assertFalse(ExcavationTargetValidator.isEligible(new TargetFacts(true, true, true, true, false, false, true)));
	}
}
