package net.hfstack.justexcavators.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.GameRules;

import net.hfstack.justexcavators.enhancement.EnhancementDropDelivery;
import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.enhancement.SmeltingDropProcessor;
import net.hfstack.justexcavators.enhancement.SmeltingRecipeResolver;
import net.hfstack.justexcavators.enhancement.SilkLootTool;
import net.hfstack.justexcavators.excavation.ExcavationBreakContext;

@Mixin(Block.class)
abstract class BlockDropMixin {
	@WrapOperation(
			method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
			)
	)
	private static void justexcavators$routeDrops(
			List<ItemStack> drops,
			Consumer<ItemStack> vanillaWorldDrop,
			Operation<Void> original,
			BlockState state,
			Level level,
			BlockPos pos,
			BlockEntity blockEntity,
			Entity entity,
			ItemStack tool
	) {
		if (!(level instanceof ServerLevel serverLevel)
				|| !serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
			original.call(drops, vanillaWorldDrop);
			return;
		}
		ExcavationBreakContext.current().ifPresentOrElse(context -> {
			boolean collector = context.enhancements().valid()
					&& context.enhancements().has(EnhancementType.COLLECTOR);
			boolean voiding = context.enhancements().valid()
					&& context.enhancements().has(EnhancementType.VOID);
			boolean smelting = context.enhancements().valid()
					&& context.enhancements().has(EnhancementType.SMELTING);
			Consumer<ItemStack> routed = drop -> {
				if (voiding) {
					EnhancementDropDelivery.deliver(
							drop, collector, true, incoming -> incoming, vanillaWorldDrop
					);
					return;
				}
				SmeltingDropProcessor.SmeltingResult processed = smelting
						? SmeltingDropProcessor.process(
								drop, input -> SmeltingRecipeResolver.smeltOne(serverLevel, input)
						)
						: new SmeltingDropProcessor.SmeltingResult(List.of(drop), false);
				if (processed.transformed()) {
					context.markDropTransformed();
				}
				for (ItemStack output : processed.outputs()) {
					EnhancementDropDelivery.deliver(
							output,
							collector,
							false,
							incoming -> {
								context.player().getInventory().add(incoming);
								return incoming;
							},
							vanillaWorldDrop
					);
				}
			};
			original.call(drops, routed);
		}, () -> original.call(drops, vanillaWorldDrop));
	}

	@WrapOperation(
			method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"
			)
	)
	private static void justexcavators$suppressVoidExperience(
			BlockState state,
			ServerLevel level,
			BlockPos pos,
			ItemStack tool,
			boolean dropExperience,
			Operation<Void> original
	) {
		boolean voiding = ExcavationBreakContext.current()
				.filter(context -> context.enhancements().valid())
				.filter(context -> context.enhancements().has(EnhancementType.VOID))
				.isPresent();
		if (EnhancementDropDelivery.shouldSpawnAfterBreak(voiding)) {
			original.call(state, level, pos, tool, dropExperience);
		}
	}

	@WrapOperation(
			method = "playerDestroy",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V"
			)
	)
	private static void justexcavators$useEffectiveLootTool(
			BlockState state,
			Level level,
			BlockPos pos,
			BlockEntity blockEntity,
			Entity entity,
			ItemStack tool,
			Operation<Void> original
	) {
		ItemStack effectiveTool = ExcavationBreakContext.current()
				.map(context -> SilkLootTool.forLoot(
						tool,
						context.enhancements(),
						Enchantments.SILK_TOUCH
				))
				.orElse(tool);
		original.call(state, level, pos, blockEntity, entity, effectiveTool);
	}
}
