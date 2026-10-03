package net.hfstack.justexcavators.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.workbench.ModMenuTypes;

public final class JustExcavatorsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.ENHANCEMENT_WORKBENCH, EnhancementWorkbenchScreen::new);
		registerVanillaWorkbenchGui();
	}

	private static void registerVanillaWorkbenchGui() {
		boolean registered = ResourceManagerHelper.registerBuiltinResourcePack(
				JustExcavators.id("vanilla_workbench_gui"),
				FabricLoader.getInstance().getModContainer(JustExcavators.MOD_ID).orElseThrow(),
				Component.translatable("resourcePack.justexcavators.vanilla_workbench_gui.name"),
				ResourcePackActivationType.NORMAL
		);
		if (!registered) {
			JustExcavators.LOGGER.warn("Could not register the vanilla workbench GUI resource pack.");
		}
	}
}
