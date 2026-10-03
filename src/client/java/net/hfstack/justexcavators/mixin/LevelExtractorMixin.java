package net.hfstack.justexcavators.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationAreaCalculator;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.excavation.ExcavationTargetValidator;
import net.hfstack.justexcavators.item.ExcavatorItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

/** Expands the vanilla block-outline shape to the active Excavator profile. */
@Mixin(LevelRenderer.class)
abstract class LevelExtractorMixin {
	private static final double PREVIEW_INSET = 0.0025D;

	@Shadow
	private Minecraft minecraft;

	@ModifyArg(
			method = "extractBlockOutline",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/state/BlockOutlineRenderState;<init>(Lnet/minecraft/core/BlockPos;ZZLnet/minecraft/world/phys/shapes/VoxelShape;)V"
			),
			index = 3
	)
	private VoxelShape justexcavators$expandSimpleOutline(VoxelShape shape) {
		return justexcavators$previewShape(shape);
	}

	@ModifyArgs(
			method = "extractBlockOutline",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/state/BlockOutlineRenderState;<init>(Lnet/minecraft/core/BlockPos;ZZLnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;)V"
			)
	)
	private void justexcavators$expandContextualOutline(Args args) {
		for (int index = 3; index <= 6; index++) {
			args.set(index, justexcavators$previewShape(args.get(index)));
		}
	}

	private VoxelShape justexcavators$previewShape(Object value) {
		if (!(value instanceof VoxelShape shape)
				|| minecraft.player == null
				|| minecraft.player.isShiftKeyDown()
				|| !(minecraft.hitResult instanceof BlockHitResult hit)) {
			return value instanceof VoxelShape shape ? shape : Shapes.empty();
		}

		ItemStack tool = minecraft.player.getMainHandItem();
		BlockPos origin = hit.getBlockPos();
		if (!(tool.getItem() instanceof ExcavatorItem)
				|| minecraft.level == null
				|| !ExcavationTargetValidator.isValidTarget(minecraft.level, origin, tool)) {
			return shape;
		}

		ExcavationMode mode = tool.getOrDefault(
				ExcavatorComponents.EXCAVATION_MODE,
				ExcavationMode.BASIC
		);
		VoxelShape preview = Shapes.empty();

		for (BlockPos target : ExcavationAreaCalculator.calculate(origin, hit.getDirection(), mode)) {
			if (!ExcavationTargetValidator.isValidTarget(minecraft.level, target, tool)) {
				continue;
			}

			VoxelShape targetShape = minecraft.level.getBlockState(target).getShape(minecraft.level, target);
			int offsetX = target.getX() - origin.getX();
			int offsetY = target.getY() - origin.getY();
			int offsetZ = target.getZ() - origin.getZ();

			for (AABB box : targetShape.toAabbs()) {
				preview = Shapes.or(preview, justexcavators$insetBox(box, offsetX, offsetY, offsetZ));
			}
		}

		return preview;
	}

	private static VoxelShape justexcavators$insetBox(AABB box, int offsetX, int offsetY, int offsetZ) {
		double insetX = Math.min(PREVIEW_INSET, (box.maxX - box.minX) * 0.25D);
		double insetY = Math.min(PREVIEW_INSET, (box.maxY - box.minY) * 0.25D);
		double insetZ = Math.min(PREVIEW_INSET, (box.maxZ - box.minZ) * 0.25D);

		return Shapes.box(
				box.minX + offsetX + insetX,
				box.minY + offsetY + insetY,
				box.minZ + offsetZ + insetZ,
				box.maxX + offsetX - insetX,
				box.maxY + offsetY - insetY,
				box.maxZ + offsetZ - insetZ
		);
	}
}
