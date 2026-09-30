package net.hfstack.justexcavators.item;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.TooltipDisplay;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.excavation.ExcavationMode;

public final class ExcavatorItem extends Item {
	private static final int DURABILITY_MULTIPLIER = 3;

	public ExcavatorItem(Item.Properties properties, ToolMaterial material) {
		super(configure(properties, material));
	}

	@Override
	public void appendHoverText(
			ItemStack stack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> textConsumer,
			TooltipFlag flag
	) {
		ExcavationMode mode = stack.getOrDefault(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.BASIC);

		textConsumer.accept(Component.translatable(
				"tooltip.justexcavators.mode",
				Component.translatable(mode.translationKey())
		).withStyle(ChatFormatting.GRAY));
		textConsumer.accept(Component.translatable(
				"tooltip.justexcavators.area",
				mode.width(),
				mode.height(),
				mode.depth()
		).withStyle(ChatFormatting.GRAY));
		ExcavatorEnhancements enhancements = stack.getOrDefault(
				ExcavatorComponents.ENHANCEMENTS,
				ExcavatorEnhancements.EMPTY
		);
		if (enhancements.has(EnhancementType.SILK)) {
			textConsumer.accept(Component.translatable("tooltip.justexcavators.enhancement.silk_touch")
					.withStyle(ChatFormatting.AQUA));
		}
		textConsumer.accept(Component.translatable("tooltip.justexcavators.precision")
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	private static Item.Properties configure(Item.Properties properties, ToolMaterial material) {
		properties
				.shovel(withScaledDurability(material), 1.5F, -3.0F)
				.component(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.BASIC)
				.component(ExcavatorComponents.ENHANCEMENTS, ExcavatorEnhancements.EMPTY);

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
