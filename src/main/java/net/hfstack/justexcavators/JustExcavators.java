package net.hfstack.justexcavators;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.block.ModBlocks;
import net.hfstack.justexcavators.item.ModCreativeTab;
import net.hfstack.justexcavators.item.ModItems;
import net.hfstack.justexcavators.recipe.ModRecipeSerializers;
import net.hfstack.justexcavators.workbench.ModMenuTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JustExcavators implements ModInitializer {
	public static final String MOD_ID = "justexcavators";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ExcavatorComponents.init();
		ModBlocks.init();
		ModItems.init();
		ModRecipeSerializers.init();
		ModMenuTypes.init();
		ModCreativeTab.init();

		LOGGER.info("Just Excavators initialized.");
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
