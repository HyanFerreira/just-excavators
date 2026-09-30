package net.hfstack.justexcavators.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import net.hfstack.justexcavators.enhancement.EnhancementType;

public final class EnhancementCoreItem extends Item {
	private final EnhancementType type;

	public EnhancementCoreItem(Properties properties, EnhancementType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public void appendHoverText(
			ItemStack stack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> textConsumer,
			TooltipFlag flag
	) {
		tooltipLines(type).forEach(textConsumer);
	}

	static List<Component> tooltipLines(EnhancementType type) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable(type.descriptionTranslationKey())
				.withStyle(ChatFormatting.GRAY));
		type.warningTranslationKey().ifPresent(key -> lines.add(
				Component.translatable(key).withStyle(ChatFormatting.RED)
		));
		return List.copyOf(lines);
	}
}
