package net.hfstack.justexcavators.workbench;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;

record EnhancementWorkbenchProjection<T>(Optional<T> slot1, Optional<T> slot2) {
	EnhancementWorkbenchProjection {
		slot1 = Objects.requireNonNull(slot1, "slot1");
		slot2 = Objects.requireNonNull(slot2, "slot2");
	}

	static <T> EnhancementWorkbenchProjection<T> from(
			ExcavatorEnhancements enhancements,
			Function<EnhancementType, T> mapper
	) {
		Objects.requireNonNull(enhancements, "enhancements");
		Objects.requireNonNull(mapper, "mapper");
		return new EnhancementWorkbenchProjection<>(
				enhancements.slot(0).map(mapper),
				enhancements.slot(1).map(mapper)
		);
	}

	static <T> EnhancementWorkbenchProjection<T> empty() {
		return new EnhancementWorkbenchProjection<>(Optional.empty(), Optional.empty());
	}

	Optional<T> slot(int index) {
		return switch (index) {
			case 0 -> slot1;
			case 1 -> slot2;
			default -> throw new IllegalArgumentException("Projection slot must be 0 or 1: " + index);
		};
	}
}
