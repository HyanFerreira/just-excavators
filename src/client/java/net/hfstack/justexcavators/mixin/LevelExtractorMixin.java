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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Expands the vanilla block-outline shape to the active Excavator profile. */
@Mixin(LevelRenderer.class)
abstract class LevelExtractorMixin {
	private static final double PREVIEW_INSET = 0.0025D;

	@ModifyExpressionValue(
			method = "renderHitOutline",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
			)
	)
	private VoxelShape justexcavators$expandHitOutline(VoxelShape shape) {
		return justexcavators$previewShape(shape);
	}

	private VoxelShape justexcavators$previewShape(VoxelShape shape) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null
				|| minecraft.player.isShiftKeyDown()
				|| !(minecraft.hitResult instanceof BlockHitResult hit)) {
			return shape;
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
