package net.hfstack.justexcavators.recipe;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementCompatibility;
import net.hfstack.justexcavators.enhancement.EnhancementType;

public final class SilkCoreApplicationPolicy {
	private SilkCoreApplicationPolicy() {
	}

	public static boolean canApply(
			ExcavatorEnhancements enhancements,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		int emptySlot = firstEmptySlot(enhancements);
		if (emptySlot < 0 || !enhancements.canSet(emptySlot, EnhancementType.SILK)) {
			return false;
		}

		ExcavatorEnhancements result = enhancements.withSlot(emptySlot, EnhancementType.SILK);
		return EnhancementCompatibility.areEnchantmentsCompatible(
				result.slot1(),
				result.slot2(),
				hasSilkTouch,
				hasFortune
		);
	}

	public static int firstEmptySlot(ExcavatorEnhancements enhancements) {
		if (enhancements.slot(0).isEmpty()) {
			return 0;
		}
		return enhancements.slot(1).isEmpty() ? 1 : -1;
	}
}
