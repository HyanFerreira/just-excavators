package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

final class ActiveEnhancementsTest {
	@Test
	void keepsValidInstalledPairsActive() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.COLLECTOR)
				.withSlot(1, EnhancementType.FILTER);
		ActiveEnhancements active = ActiveEnhancements.resolve(installed, false, true);

		assertTrue(active.valid());
		assertTrue(active.has(EnhancementType.COLLECTOR));
		assertTrue(active.has(EnhancementType.FILTER));
		assertEquals(installed, active.enhancements());
	}

	@Test
	void emptySlotsAreValidAndInactive() {
		ActiveEnhancements active = ActiveEnhancements.resolve(ExcavatorEnhancements.EMPTY, true, true);
		assertTrue(active.valid());
		for (EnhancementType type : EnhancementType.values()) assertFalse(active.has(type));
	}

	@Test
	void conflictDisablesEveryCoreWithoutChangingInstalledData() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.FILTER);
		ActiveEnhancements active = ActiveEnhancements.resolve(installed, false, true);

		assertFalse(active.valid());
		for (EnhancementType type : EnhancementType.values()) assertFalse(active.has(type));
		assertEquals(EnhancementType.SILK, installed.slot(0).orElseThrow());
	}
}
