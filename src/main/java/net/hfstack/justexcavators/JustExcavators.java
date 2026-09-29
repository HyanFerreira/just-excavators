package net.hfstack.justexcavators;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.item.ModCreativeTab;
import net.hfstack.justexcavators.item.ModItems;
import net.hfstack.justexcavators.recipe.ModRecipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JustExcavators implements ModInitializer {
	public static final String MOD_ID = "justexcavators";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ExcavatorComponents.init();
		ModItems.init();
		ModRecipes.init();
		ModCreativeTab.init();

		LOGGER.info("Just Excavators initialized.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
