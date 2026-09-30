package net.hfstack.justexcavators.workbench;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;

final class EnhancementWorkbenchProjectionTest {
	@Test
	void projectsEmptyOneTwoAndGappedFixedSlotStatesExactly() {
		assertProjection(ExcavatorEnhancements.EMPTY, Optional.empty(), Optional.empty());
		assertProjection(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK),
				Optional.of("silk"),
				Optional.empty()
		);
		assertProjection(
				ExcavatorEnhancements.EMPTY
						.withSlot(0, EnhancementType.COLLECTOR)
						.withSlot(1, EnhancementType.FILTER),
				Optional.of("collector"),
				Optional.of("filter")
		);
		assertProjection(
				ExcavatorEnhancements.EMPTY.withSlot(1, EnhancementType.VOID),
				Optional.empty(),
				Optional.of("void")
		);
	}

	@Test
	void closureProjectionIsEmptyAndCarriesNoRecoverableCoreValues() {
		EnhancementWorkbenchProjection<String> cleared = EnhancementWorkbenchProjection.empty();
		assertEquals(Optional.empty(), cleared.slot(0));
		assertEquals(Optional.empty(), cleared.slot(1));
	}

	@Test
	void rejectsInvalidProjectionIndexes() {
		EnhancementWorkbenchProjection<String> projection = EnhancementWorkbenchProjection.empty();
		assertThrows(IllegalArgumentException.class, () -> projection.slot(-1));
		assertThrows(IllegalArgumentException.class, () -> projection.slot(2));
	}

	private static void assertProjection(
			ExcavatorEnhancements enhancements,
			Optional<String> expectedFirst,
			Optional<String> expectedSecond
	) {
		EnhancementWorkbenchProjection<String> projection = EnhancementWorkbenchProjection.from(
				enhancements,
				EnhancementType::serializedName
		);
		assertEquals(expectedFirst, projection.slot(0));
		assertEquals(expectedSecond, projection.slot(1));
	}
}
