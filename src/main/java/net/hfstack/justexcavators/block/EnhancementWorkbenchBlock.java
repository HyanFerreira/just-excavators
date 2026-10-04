package net.hfstack.justexcavators.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.hfstack.justexcavators.workbench.EnhancementWorkbenchMenu;

public final class EnhancementWorkbenchBlock extends Block {
	private static final Component TITLE = Component.translatable(
			"container.justexcavators.enhancement_workbench"
	);

	public EnhancementWorkbenchBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hit
	) {
		if (!level.isClientSide()) {
			player.openMenu(new SimpleMenuProvider(
					(id, inventory, ignored) -> new EnhancementWorkbenchMenu(
							id,
							inventory,
							ContainerLevelAccess.create(level, pos)
					),
					TITLE
			));
		}
		return InteractionResult.SUCCESS;
	}
}
