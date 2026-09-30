package net.hfstack.justexcavators.client;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.gui.screens.MenuScreens;

import net.hfstack.justexcavators.workbench.ModMenuTypes;

public final class JustExcavatorsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuTypes.ENHANCEMENT_WORKBENCH, EnhancementWorkbenchScreen::new);
	}
}
