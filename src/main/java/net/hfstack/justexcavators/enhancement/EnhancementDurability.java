package net.hfstack.justexcavators.enhancement;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class EnhancementDurability {
	private EnhancementDurability() {
	}

	public static int extraPotentialDamage(boolean smeltingActive, boolean transformedAnyDrop) {
		return smeltingActive && transformedAnyDrop ? 1 : 0;
	}

	public static int extraPotentialDamage(
			boolean smeltingActive,
			boolean transformedAnyDrop,
			boolean creative
	) {
		return creative ? 0 : extraPotentialDamage(smeltingActive, transformedAnyDrop);
	}

	public static void apply(
			ItemStack originalTool,
			ServerPlayer player,
			boolean smeltingActive,
			boolean transformedAnyDrop
	) {
		int damage = extraPotentialDamage(
				smeltingActive, transformedAnyDrop, player.hasInfiniteMaterials()
		);
		if (damage > 0 && player.getMainHandItem() == originalTool && !originalTool.isEmpty()) {
			originalTool.hurtAndBreak(damage, player, EquipmentSlot.MAINHAND);
		}
	}
}
