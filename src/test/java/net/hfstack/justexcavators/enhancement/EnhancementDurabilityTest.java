package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class EnhancementDurabilityTest {
	@Test
	void chargesOnlyWhenSmeltingActuallyTransformsADrop() {
		assertEquals(0, EnhancementDurability.extraPotentialDamage(false, false));
		assertEquals(0, EnhancementDurability.extraPotentialDamage(false, true));
		assertEquals(0, EnhancementDurability.extraPotentialDamage(true, false));
		assertEquals(1, EnhancementDurability.extraPotentialDamage(true, true));
	}

	@Test
	void chargesOnePotentialPointRegardlessOfTransformedUnitCount() {
		assertEquals(1, EnhancementDurability.extraPotentialDamage(true, true));
	}

	@Test
	void creativeBypassesTheExtraPoint() {
		assertEquals(0, EnhancementDurability.extraPotentialDamage(true, true, true));
		assertEquals(1, EnhancementDurability.extraPotentialDamage(true, true, false));
	}
}
