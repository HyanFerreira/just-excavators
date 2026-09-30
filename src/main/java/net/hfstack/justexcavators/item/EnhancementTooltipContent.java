package net.hfstack.justexcavators.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public final class EnhancementTooltipContent {
	private static final int SLOT_COUNT = 2;

	private EnhancementTooltipContent() {
	}

	public static List<Component> lines(ExcavatorEnhancements enhancements) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable(
				"tooltip.justexcavators.enhancements",
				enhancements.size(),
				SLOT_COUNT
		).withStyle(ChatFormatting.AQUA));
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			int slotNumber = slot + 1;
			enhancements.slot(slot).ifPresent(type -> lines.add(Component.translatable(
					"tooltip.justexcavators.enhancement_slot",
					slotNumber,
					Component.translatable(type.translationKey())
			).withStyle(ChatFormatting.GRAY)));
		}
		return List.copyOf(lines);
	}
}
