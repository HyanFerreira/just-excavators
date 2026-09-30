package net.hfstack.justexcavators.item;

import java.util.Objects;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public final class LegacySilkMigration {
	private LegacySilkMigration() {
	}

	public static boolean migrate(
			ItemStack stack,
			DataComponentType<ExcavatorEnhancements> componentType
	) {
		Objects.requireNonNull(stack, "stack");
		Objects.requireNonNull(componentType, "componentType");
		ExcavatorEnhancements enhancements = stack.getOrDefault(
				componentType,
				ExcavatorEnhancements.EMPTY
		);
		if (!enhancements.legacySilkMigrationRequired()) {
			return false;
		}

		EnchantmentHelper.updateEnchantments(
				stack,
				mutable -> mutable.removeIf(holder -> holder.is(Enchantments.SILK_TOUCH))
		);
		stack.set(componentType, enhancements.canonical());
		return true;
	}
}
