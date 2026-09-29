package net.hfstack.justexcavators.mixin;

import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.enhancement.SilkCoreGrindstoneBehavior;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
abstract class GrindstoneResultSlotMixin {
	@Inject(method = "getExperienceFromItem", at = @At("HEAD"), cancellable = true)
	private void justexcavators$excludeSilkCoreFromExperience(
			ItemStack stack,
			CallbackInfoReturnable<Integer> callback
	) {
		if (SilkCoreGrindstoneBehavior.hasSilkCore(stack)) {
			callback.setReturnValue(SilkCoreGrindstoneBehavior.removableEnchantmentExperience(stack));
		}
	}
}
