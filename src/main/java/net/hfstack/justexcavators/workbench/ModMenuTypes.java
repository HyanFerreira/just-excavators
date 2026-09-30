package net.hfstack.justexcavators.workbench;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import net.hfstack.justexcavators.JustExcavators;

public final class ModMenuTypes {
	public static final MenuType<EnhancementWorkbenchMenu> ENHANCEMENT_WORKBENCH = Registry.register(
			BuiltInRegistries.MENU,
			ResourceKey.create(Registries.MENU, JustExcavators.id("enhancement_workbench")),
			new MenuType<>(EnhancementWorkbenchMenu::new, FeatureFlags.VANILLA_SET)
	);

	private ModMenuTypes() {
	}

	public static void init() {
	}
}
