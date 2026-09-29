package net.hfstack.justexcavators.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.enhancement.SilkCoreGrindstoneBehavior;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
abstract class GrindstoneMenuMixin {
	@Unique
	private Holder<Enchantment> justexcavators$silkTouchFromCore;

	@Inject(method = "removeNonCursesFrom", at = @At("HEAD"))
	private void justexcavators$captureSilkCoreEnchantment(
			ItemStack stack,
			CallbackInfoReturnable<ItemStack> callback
	) {
		justexcavators$silkTouchFromCore = null;
		if (SilkCoreGrindstoneBehavior.hasSilkCore(stack)) {
			justexcavators$silkTouchFromCore = stack.getEnchantments().keySet().stream()
					.filter(enchantment -> enchantment.is(Enchantments.SILK_TOUCH))
					.findFirst()
					.orElse(null);
		}
	}

	@Inject(method = "removeNonCursesFrom", at = @At("RETURN"))
	private void justexcavators$restoreSilkCoreEnchantment(
			ItemStack stack,
			CallbackInfoReturnable<ItemStack> callback
	) {
		ItemStack result = callback.getReturnValue();
		if (justexcavators$silkTouchFromCore != null
				&& !result.isEmpty()
				&& SilkCoreGrindstoneBehavior.hasSilkCore(result)) {
			result.enchant(justexcavators$silkTouchFromCore, 1);
		}
		justexcavators$silkTouchFromCore = null;
	}
}
