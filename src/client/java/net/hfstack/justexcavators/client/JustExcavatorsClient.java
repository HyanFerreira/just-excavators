package net.hfstack.justexcavators.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.workbench.ModMenuTypes;

public final class JustExcavatorsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.ENHANCEMENT_WORKBENCH, EnhancementWorkbenchScreen::new);
		registerDecoratedWorkbenchGui();
	}

	private static void registerDecoratedWorkbenchGui() {
		boolean registered = ResourceLoader.registerBuiltinPack(
				JustExcavators.id("decorated_workbench_gui"),
				FabricLoader.getInstance().getModContainer(JustExcavators.MOD_ID).orElseThrow(),
				Component.translatable("resourcePack.justexcavators.decorated_workbench_gui.name"),
				PackActivationType.NORMAL
		);
		if (!registered) {
			JustExcavators.LOGGER.warn("Could not register the decorated workbench GUI resource pack.");
		}
	}
}
