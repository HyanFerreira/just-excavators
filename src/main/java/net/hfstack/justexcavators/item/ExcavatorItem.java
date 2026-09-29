package net.hfstack.justexcavators.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public final class ExcavatorItem extends Item {
	private static final int DURABILITY_MULTIPLIER = 3;

	public ExcavatorItem(Item.Properties properties, ToolMaterial material) {
		super(configure(properties, material));
	}

	private static Item.Properties configure(Item.Properties properties, ToolMaterial material) {
		properties.shovel(withScaledDurability(material), 1.5F, -3.0F);

		if (material == ToolMaterial.NETHERITE) {
			properties.fireResistant();
		}

		return properties;
	}

	private static ToolMaterial withScaledDurability(ToolMaterial material) {
		return new ToolMaterial(
				material.incorrectBlocksForDrops(),
				material.durability() * DURABILITY_MULTIPLIER,
				material.speed(),
				material.attackDamageBonus(),
				material.enchantmentValue(),
				material.repairItems()
		);
	}
}
