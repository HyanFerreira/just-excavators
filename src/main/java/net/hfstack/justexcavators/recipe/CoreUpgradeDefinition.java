package net.hfstack.justexcavators.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.hfstack.justexcavators.excavation.ExcavationMode;

public record CoreUpgradeDefinition(ExcavationMode mode) {
	public static final MapCodec<CoreUpgradeDefinition> MAP_CODEC = ExcavationMode.CODEC
			.fieldOf("mode")
			.xmap(CoreUpgradeDefinition::new, CoreUpgradeDefinition::mode);
	public static final Codec<CoreUpgradeDefinition> CODEC = MAP_CODEC.codec();

	public static boolean canApply(ExcavationMode currentMode, ExcavationMode targetMode) {
		return currentMode != targetMode;
	}
}
