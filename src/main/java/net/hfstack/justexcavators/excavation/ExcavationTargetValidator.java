package net.hfstack.justexcavators.excavation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.hfstack.justexcavators.item.ExcavatorItem;
import net.hfstack.justexcavators.registry.ModTags;

public final class ExcavationTargetValidator {
	private ExcavationTargetValidator() {
	}

	public static boolean isValidTarget(BlockGetter level, BlockPos pos, ItemStack tool) {
		BlockState state = level.getBlockState(pos);
		return isEligible(new TargetFacts(
				tool.getItem() instanceof ExcavatorItem,
				state.is(BlockTags.MINEABLE_WITH_SHOVEL),
				tool.isCorrectToolForDrops(state),
				state.getDestroySpeed(level, pos) >= 0.0F,
				state.hasBlockEntity(),
				state.is(ModTags.EXCAVATOR_NO_AOE),
				!state.getFluidState().isEmpty()
		));
	}

	static boolean isEligible(TargetFacts facts) {
		return facts.excavator()
				&& facts.shovelMineable()
				&& facts.correctToolForDrops()
				&& facts.breakable()
				&& !facts.blockEntity()
				&& !facts.excluded()
				&& !facts.fluidPresent();
	}

	record TargetFacts(
			boolean excavator,
			boolean shovelMineable,
			boolean correctToolForDrops,
			boolean breakable,
			boolean blockEntity,
			boolean excluded,
			boolean fluidPresent
	) {
	}
}
