package net.hfstack.justexcavators.enhancement;

import java.util.Objects;

import net.minecraft.world.level.block.Block;

public final class FilterCorePolicy {
	private FilterCorePolicy() {
	}

	public static boolean shouldProcess(
			boolean additional,
			ActiveEnhancements enhancements,
			Block centralBlock,
			Block candidateBlock
	) {
		Objects.requireNonNull(enhancements, "enhancements");
		Objects.requireNonNull(centralBlock, "centralBlock");
		Objects.requireNonNull(candidateBlock, "candidateBlock");
		return !additional
				|| !enhancements.valid()
				|| !enhancements.has(EnhancementType.FILTER)
				|| centralBlock == candidateBlock;
	}
}
