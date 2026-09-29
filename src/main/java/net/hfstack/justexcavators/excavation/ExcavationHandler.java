package net.hfstack.justexcavators.excavation;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;

public final class ExcavationHandler {
	private ExcavationHandler() {
	}

	public static void excavate(
			ServerPlayerGameMode gameMode,
			ServerLevel level,
			ServerPlayer player,
			BlockPos origin,
			Direction hitFace,
			ExcavationMode excavationMode,
			ItemStack originalTool
	) {
		List<BlockPos> positions = ExcavationAreaCalculator.calculate(origin, hitFace, excavationMode);

		for (int index = 1; index < positions.size(); index++) {
			if (!canContinueWith(player, originalTool)) {
				return;
			}

			BlockPos target = positions.get(index);
			if (!canPlayerModify(level, player, target)
					|| !ExcavationTargetValidator.isValidTarget(level, target, originalTool)) {
				continue;
			}

			gameMode.destroyBlock(target);
		}
	}

	private static boolean canContinueWith(ServerPlayer player, ItemStack originalTool) {
		ItemStack mainHand = player.getMainHandItem();
		return ExcavationExecutionPolicy.canContinue(
				mainHand == originalTool,
				!mainHand.isEmpty() && !mainHand.isBroken(),
				mainHand.nextDamageWillBreak(),
				player.hasInfiniteMaterials()
		);
	}

	private static boolean canPlayerModify(ServerLevel level, ServerPlayer player, BlockPos target) {
		return !level.getServer().isUnderSpawnProtection(level, target, player)
				&& level.mayInteract(player, target);
	}
}
