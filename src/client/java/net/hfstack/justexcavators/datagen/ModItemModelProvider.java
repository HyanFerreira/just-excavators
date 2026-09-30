package net.hfstack.justexcavators.datagen;

import java.util.List;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.item.ModItems;

public final class ModItemModelProvider extends FabricModelProvider {
	public ModItemModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		generateExcavator(generators, ModItems.STONE_EXCAVATOR, "stone");
		generateExcavator(generators, ModItems.COPPER_EXCAVATOR, "copper");
		generateExcavator(generators, ModItems.IRON_EXCAVATOR, "iron");
		generateExcavator(generators, ModItems.GOLDEN_EXCAVATOR, "gold");
		generateExcavator(generators, ModItems.DIAMOND_EXCAVATOR, "diamond");
		generateExcavator(generators, ModItems.NETHERITE_EXCAVATOR, "netherite");

		generateCore(generators, ModItems.DEEP_EXCAVATION_CORE, "deep_excavation_core");
		generateCore(generators, ModItems.WIDE_EXCAVATION_CORE, "wide_excavation_core");
		generateCore(generators, ModItems.ADVANCED_EXCAVATION_CORE, "advanced_excavation_core");
		generateCore(generators, ModItems.CORE_HOUSING, "core_housing");
		generateCore(generators, ModItems.SILK_CORE, "silk_core");
		generateCore(generators, ModItems.COLLECTOR_CORE, "collector_core");
		generateCore(generators, ModItems.SMELTING_CORE, "smelting_core");
		generateCore(generators, ModItems.FILTER_CORE, "filter_core");
		generateCore(generators, ModItems.VOID_CORE, "void_core");
		generators.itemModelOutput.accept(
				ModItems.ENHANCEMENT_WORKBENCH,
				ItemModelUtils.plainModel(JustExcavators.id("block/enhancement_workbench"))
		);
	}

	private static void generateExcavator(ItemModelGenerators generators, Item item, String material) {
		ItemModel.Unbaked basic = ItemModelUtils.plainModel(createModel(
				generators,
				material + "_excavator",
				true
		));
		ItemModel.Unbaked deep = ItemModelUtils.plainModel(createModel(
				generators,
				material + "_deep_excavator",
				true
		));
		ItemModel.Unbaked wide = ItemModelUtils.plainModel(createModel(
				generators,
				material + "_wide_excavator",
				true
		));
		ItemModel.Unbaked advanced = ItemModelUtils.plainModel(createModel(
				generators,
				material + "_advanced_excavator",
				true
		));

		generators.itemModelOutput.accept(item, ItemModelUtils.select(
				new ComponentContents<>(ExcavatorComponents.EXCAVATION_MODE),
				basic,
				List.of(
						ItemModelUtils.when(ExcavationMode.DEEP, deep),
						ItemModelUtils.when(ExcavationMode.WIDE, wide),
						ItemModelUtils.when(ExcavationMode.ADVANCED, advanced)
				)
		));
	}

	private static void generateCore(ItemModelGenerators generators, Item item, String textureName) {
		generateCore(generators, item, textureName, textureName);
	}

	private static void generateCore(
			ItemModelGenerators generators,
			Item item,
			String modelName,
			String textureName
	) {
		Identifier model = ModelTemplates.FLAT_ITEM.create(
				JustExcavators.id("item/" + modelName),
				TextureMapping.layer0(new Material(JustExcavators.id("item/" + textureName))),
				generators.modelOutput
		);
		generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
	}

	private static Identifier createModel(
			ItemModelGenerators generators,
			String textureName,
			boolean handheld
	) {
		Identifier id = JustExcavators.id("item/" + textureName);
		return (handheld ? ModelTemplates.FLAT_HANDHELD_ITEM : ModelTemplates.FLAT_ITEM).create(
				id,
				TextureMapping.layer0(new Material(id)),
				generators.modelOutput
		);
	}
}
