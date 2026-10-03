package net.hfstack.justexcavators.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;

public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			JustExcavators.id("main")
	);

	public static final CreativeModeTab TAB = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			KEY,
			FabricItemGroup.builder()
					.title(Component.translatable("itemGroup.justexcavators"))
					.icon(ModCreativeTab::createIcon)
					.displayItems((parameters, output) -> {
						ModItems.EXCAVATORS.forEach(excavator -> {
							for (ExcavationMode mode : ExcavationMode.values()) {
								ItemStack stack = new ItemStack(excavator);
								stack.set(ExcavatorComponents.EXCAVATION_MODE, mode);
								output.accept(stack);
							}
						});
						output.accept(ModItems.CORE_HOUSING);
						ModItems.CORES.forEach(output::accept);
						output.accept(ModItems.ENHANCEMENT_WORKBENCH);
					})
					.build()
	);

	private ModCreativeTab() {
	}

	private static ItemStack createIcon() {
		ItemStack icon = new ItemStack(ModItems.DIAMOND_EXCAVATOR);
		icon.set(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.ADVANCED);
		return icon;
	}

	public static void init() {
		// Loading this class performs the creative tab registration.
	}
}
