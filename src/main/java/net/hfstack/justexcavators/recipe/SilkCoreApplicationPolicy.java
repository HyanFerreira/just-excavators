package net.hfstack.justexcavators.recipe;

public final class SilkCoreApplicationPolicy {
	private SilkCoreApplicationPolicy() {
	}

	public static boolean canApply(boolean hasSilkEnhancement, boolean hasSilkTouch, boolean hasFortune) {
		return !hasSilkEnhancement && !hasSilkTouch && !hasFortune;
	}
}
