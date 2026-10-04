package net.hfstack.justexcavators.item;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public final class LegacySilkMigration {
	private LegacySilkMigration() {
	}

	public static boolean migrate(ItemStack stack) {
		ExcavatorEnhancements enhancements = net.hfstack.justexcavators.component.ExcavatorComponents.getEnhancements(stack);
		if (!enhancements.legacySilkMigrationRequired()) {
			return false;
		}

		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		enchantments.remove(Enchantments.SILK_TOUCH);
		EnchantmentHelper.setEnchantments(enchantments, stack);
		net.hfstack.justexcavators.component.ExcavatorComponents.setEnhancements(stack, enhancements.canonical());
		return true;
	}
}
