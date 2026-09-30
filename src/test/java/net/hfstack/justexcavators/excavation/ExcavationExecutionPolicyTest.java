package net.hfstack.justexcavators.excavation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.excavation.ExcavationExecutionPolicy.StartFacts;

final class ExcavationExecutionPolicyTest {
	@Test
	void startsOnlyAfterAnEligibleCentralBlockWasDestroyedNormally() {
		assertTrue(ExcavationExecutionPolicy.canStart(new StartFacts(true, true, false, false)));

		assertFalse(ExcavationExecutionPolicy.canStart(new StartFacts(false, true, false, false)));
		assertFalse(ExcavationExecutionPolicy.canStart(new StartFacts(true, false, false, false)));
		assertFalse(ExcavationExecutionPolicy.canStart(new StartFacts(true, true, true, false)));
		assertFalse(ExcavationExecutionPolicy.canStart(new StartFacts(true, true, false, true)));
	}

	@Test
	void continuesOnlyWhileTheOriginalUsableToolRemainsInTheMainHand() {
		assertTrue(ExcavationExecutionPolicy.canContinue(true, true, false, false));
		assertFalse(ExcavationExecutionPolicy.canContinue(false, true, false, false));
		assertFalse(ExcavationExecutionPolicy.canContinue(true, false, false, false));
	}

	@Test
	void preservesTheLastDurabilityPointOutsideCreative() {
		assertFalse(ExcavationExecutionPolicy.canContinue(true, true, true, false));
		assertTrue(ExcavationExecutionPolicy.canContinue(true, true, true, true));
	}

	@Test
	void stopsAoeAfterAnEnhancementBreaksOrReplacesTheOriginalTool() {
		assertFalse(ExcavationExecutionPolicy.canContinue(true, false, false, false));
		assertFalse(ExcavationExecutionPolicy.canContinue(false, true, false, false));
	}

	@Test
	void retainsCapturedHitOnlyWhileVanillaStillTracksThatPosition() {
		assertTrue(ExcavationExecutionPolicy.shouldRetainHit(true, false));
		assertTrue(ExcavationExecutionPolicy.shouldRetainHit(false, true));
		assertFalse(ExcavationExecutionPolicy.shouldRetainHit(false, false));
	}
}
