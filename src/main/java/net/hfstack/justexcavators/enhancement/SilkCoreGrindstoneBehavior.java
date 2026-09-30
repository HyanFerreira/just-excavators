package net.hfstack.justexcavators.enhancement;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public final class SilkCoreGrindstoneBehavior {
	private SilkCoreGrindstoneBehavior() {
	}

	public static boolean hasSilkCore(ItemStack stack) {
		return stack.getOrDefault(ExcavatorComponents.ENHANCEMENTS, ExcavatorEnhancements.EMPTY)
				.has(EnhancementType.SILK);
	}

	public static int removableEnchantmentExperience(ItemStack stack) {
		int experience = 0;
		for (var entry : stack.getEnchantments().entrySet()) {
			if (!entry.getKey().is(EnchantmentTags.CURSE) && !entry.getKey().is(Enchantments.SILK_TOUCH)) {
				experience += entry.getKey().value().getMinCost(entry.getIntValue());
			}
		}
		return experience;
	}
}
