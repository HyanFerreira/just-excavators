package net.hfstack.justexcavators.mixin;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationExecutionPolicy;
import net.hfstack.justexcavators.excavation.ExcavationHandler;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.excavation.ExcavationTargetValidator;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerLevel level;

	@Shadow
	@Final
	protected ServerPlayer player;

	@Shadow
	private boolean isDestroyingBlock;

	@Shadow
	private BlockPos destroyPos;

	@Shadow
	private boolean hasDelayedDestroy;

	@Shadow
	private BlockPos delayedDestroyPos;

	@Unique
	private Map<BlockPos, Direction> justexcavators$trackedHits;

	@Unique
	private BlockPos justexcavators$packetHitPos;

	@Unique
	private Direction justexcavators$packetHitFace;

	@Unique
	private BreakContext justexcavators$breakContext;

	@Unique
	private boolean justexcavators$excavating;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	private void justexcavators$captureHitFace(
			BlockPos pos,
			Action action,
			Direction direction,
			int worldHeight,
			int sequence,
			CallbackInfo callback
	) {
		if (action == Action.START_DESTROY_BLOCK || action == Action.CHANGE_DESTROY_DIRECTION) {
			justexcavators$packetHitPos = pos.immutable();
			justexcavators$packetHitFace = direction;
		}
	}

	@Inject(
			method = "handleBlockBreakAction",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;destroyDirection:Lnet/minecraft/core/Direction;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void justexcavators$trackAcceptedHit(
			BlockPos pos,
			Action action,
			Direction direction,
			int worldHeight,
			int sequence,
			CallbackInfo callback
	) {
		justexcavators$trackedHits().put(pos.immutable(), direction);
	}

	@Inject(method = "handleBlockBreakAction", at = @At("RETURN"))
	private void justexcavators$discardRejectedHit(
			BlockPos pos,
			Action action,
			Direction direction,
			int worldHeight,
			int sequence,
			CallbackInfo callback
	) {
		justexcavators$packetHitPos = null;
		justexcavators$packetHitFace = null;
		justexcavators$discardHitsVanillaStoppedTracking();
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void justexcavators$discardFinishedHit(CallbackInfo callback) {
		justexcavators$discardHitsVanillaStoppedTracking();
	}

	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void justexcavators$prepareExcavation(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
		if (justexcavators$excavating) {
			return;
		}

		ItemStack tool = player.getMainHandItem();
		Direction hitFace = pos.equals(justexcavators$packetHitPos)
				? justexcavators$packetHitFace
				: justexcavators$trackedHits().get(pos);
		justexcavators$breakContext = new BreakContext(
				pos.immutable(),
				hitFace,
				tool,
				tool.getOrDefault(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.BASIC),
				hitFace != null && ExcavationTargetValidator.isValidTarget(level, pos, tool),
				player.isShiftKeyDown()
		);
	}

	@Inject(method = "destroyBlock", at = @At("RETURN"))
	private void justexcavators$excavateAfterCentralBlock(
			BlockPos pos,
			CallbackInfoReturnable<Boolean> callback
	) {
		if (justexcavators$excavating) {
			return;
		}

		BreakContext context = justexcavators$breakContext;
		justexcavators$breakContext = null;
		justexcavators$trackedHits().remove(pos);

		if (context == null || !ExcavationExecutionPolicy.canStart(
				new ExcavationExecutionPolicy.StartFacts(
						callback.getReturnValue(),
						context.centralEligible(),
						context.sneaking(),
						false
				)
		)) {
			return;
		}

		justexcavators$excavating = true;
		try {
			ExcavationHandler.excavate(
					(ServerPlayerGameMode) (Object) this,
					level,
					player,
					context.origin(),
					context.hitFace(),
					context.mode(),
					context.tool()
			);
		} finally {
			justexcavators$excavating = false;
		}
	}

	@Unique
	private void justexcavators$discardHitsVanillaStoppedTracking() {
		justexcavators$trackedHits().keySet().removeIf(pos -> !ExcavationExecutionPolicy.shouldRetainHit(
				isDestroyingBlock && pos.equals(destroyPos),
				hasDelayedDestroy && pos.equals(delayedDestroyPos)
		));
	}

	@Unique
	private Map<BlockPos, Direction> justexcavators$trackedHits() {
		if (justexcavators$trackedHits == null) {
			justexcavators$trackedHits = new HashMap<>();
		}
		return justexcavators$trackedHits;
	}

	@Unique
	private record BreakContext(
			BlockPos origin,
			Direction hitFace,
			ItemStack tool,
			ExcavationMode mode,
			boolean centralEligible,
			boolean sneaking
	) {
	}
}
