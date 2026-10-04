package net.hfstack.justexcavators.enhancement;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class SilkLootTool {
	private SilkLootTool() {
	}

	public static ItemStack forLoot(
			ItemStack original,
			ActiveEnhancements enhancements,
			Enchantment silkTouch
	) {
		if (!enhancements.has(EnhancementType.SILK)) {
			return original;
		}

		ItemStack effective = original.copyWithCount(1);
		effective.enchant(silkTouch, 1);
		return effective;
	}
}
