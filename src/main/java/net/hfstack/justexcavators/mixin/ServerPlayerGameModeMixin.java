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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationExecutionPolicy;
import net.hfstack.justexcavators.excavation.ExcavationBreakContext;
import net.hfstack.justexcavators.excavation.ExcavationHandler;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.excavation.ExcavationTargetValidator;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.ActiveEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementDurability;
import net.hfstack.justexcavators.enhancement.EnhancementType;

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
		if (action == Action.START_DESTROY_BLOCK) {
			justexcavators$packetHitPos = pos.immutable();
			justexcavators$packetHitFace = direction;
		}
	}

	@Inject(
			method = "handleBlockBreakAction",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;destroyPos:Lnet/minecraft/core/BlockPos;",
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
		ExcavatorEnhancements installed = tool.getOrDefault(
				ExcavatorComponents.ENHANCEMENTS,
				ExcavatorEnhancements.EMPTY
		);
		ActiveEnhancements enhancements = ActiveEnhancements.resolve(
				installed,
				hasEnchantment(tool, Enchantments.SILK_TOUCH),
				hasEnchantment(tool, Enchantments.FORTUNE)
		);
		Direction hitFace = pos.equals(justexcavators$packetHitPos)
				? justexcavators$packetHitFace
				: justexcavators$trackedHits().get(pos);
		justexcavators$breakContext = new BreakContext(
				pos.immutable(),
				hitFace,
				tool,
				enhancements,
				level.getBlockState(pos),
				tool.getOrDefault(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.BASIC),
				hitFace != null && ExcavationTargetValidator.isValidTarget(level, pos, tool),
				player.isShiftKeyDown()
		);
	}

	@WrapOperation(
			method = "destroyBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private void justexcavators$scopeCentralBreak(
			Block block,
			Level level,
			Player player,
			BlockPos pos,
			BlockState state,
			BlockEntity blockEntity,
			ItemStack lootTool,
			Operation<Void> original
	) {
		BreakContext context = justexcavators$breakContext;
		if (context == null || ExcavationBreakContext.current().isPresent()) {
			original.call(block, level, player, pos, state, blockEntity, lootTool);
			return;
		}
		try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(
				(ServerPlayer) player,
				context.tool(),
				context.enhancements(),
				context.origin(),
				context.centralState(),
				false
		)) {
			original.call(block, level, player, pos, state, blockEntity, lootTool);
			ExcavationBreakContext.current().ifPresent(breakScope -> EnhancementDurability.apply(
					context.tool(),
					(ServerPlayer) player,
					context.enhancements().has(EnhancementType.SMELTING),
					breakScope.transformedAnyDrop()
			));
		}
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
					context.tool(),
					context.enhancements(),
					context.centralState()
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
	private static boolean hasEnchantment(
			ItemStack stack,
			net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> enchantment
	) {
		return stack.getEnchantments().keySet().stream().anyMatch(holder -> holder.is(enchantment));
	}

	@Unique
	private record BreakContext(
			BlockPos origin,
			Direction hitFace,
			ItemStack tool,
			ActiveEnhancements enhancements,
			BlockState centralState,
			ExcavationMode mode,
			boolean centralEligible,
			boolean sneaking
	) {
	}
}
