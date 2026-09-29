package net.hfstack.justexcavators.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.JustExcavators;

public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			JustExcavators.id("main")
	);

	public static final CreativeModeTab TAB = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			KEY,
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.justexcavators"))
					.icon(() -> new ItemStack(ModItems.IRON_EXCAVATOR))
					.displayItems((parameters, output) -> {
						ModItems.EXCAVATORS.forEach(output::accept);
						ModItems.CORES.forEach(output::accept);
					})
					.build()
	);

	private ModCreativeTab() {
	}

	public static void init() {
		// Loading this class performs the creative tab registration.
	}
}
