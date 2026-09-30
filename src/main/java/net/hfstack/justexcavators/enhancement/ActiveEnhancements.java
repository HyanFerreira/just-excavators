package net.hfstack.justexcavators.enhancement;

import java.util.Objects;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public record ActiveEnhancements(ExcavatorEnhancements enhancements, boolean valid) {
	public static final ActiveEnhancements EMPTY = new ActiveEnhancements(ExcavatorEnhancements.EMPTY, true);

	public ActiveEnhancements {
		enhancements = Objects.requireNonNull(enhancements, "enhancements");
	}

	public static ActiveEnhancements resolve(
			ExcavatorEnhancements installed,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		Objects.requireNonNull(installed, "installed");
		boolean compatible = EnhancementCompatibility.areEnchantmentsCompatible(
				installed.slot(0), installed.slot(1), hasSilkTouch, hasFortune
		);
		return compatible
				? new ActiveEnhancements(installed, true)
				: new ActiveEnhancements(ExcavatorEnhancements.EMPTY, false);
	}

	public boolean has(EnhancementType type) {
		return enhancements.has(type);
	}
}
