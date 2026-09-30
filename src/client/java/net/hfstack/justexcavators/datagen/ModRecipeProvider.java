package net.hfstack.justexcavators.datagen;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.item.ModItems;

public final class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registryLookupFuture
	) {
		super(output, registryLookupFuture);
	}

	@Override
	public String getName() {
		return "Just Excavators Recipes";
	}

	@Override
	protected RecipeProvider createRecipeProvider(
			HolderLookup.Provider registries,
			BootstrapContext<Recipe<?>> recipes,
			BootstrapContext<Advancement> advancements
	) {
		return new RecipeProvider(recipes, advancements) {
			@Override
			public void buildRecipes() {
				buildCores();
				buildEnhancementWorkbench();
				buildExcavators();
				buildNetheriteUpgrade();
			}

			private void buildCores() {
				shaped(RecipeCategory.MISC, ModItems.CORE_HOUSING)
						.define('O', Items.IRON_NUGGET)
						.define('H', Items.IRON_INGOT)
						.pattern("OHO")
						.pattern("H H")
						.pattern("OHO")
						.unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
						.save(output);

				shaped(RecipeCategory.MISC, ModItems.DEEP_EXCAVATION_CORE)
						.define('R', Items.REDSTONE)
						.define('I', Items.IRON_BLOCK)
						.define('E', ModItems.NETHERITE_EXCAVATOR)
						.pattern("RRR")
						.pattern("IEI")
						.pattern("RRR")
						.unlockedBy("has_netherite_excavator", has(ModItems.NETHERITE_EXCAVATOR))
						.save(output);

				shaped(RecipeCategory.MISC, ModItems.WIDE_EXCAVATION_CORE)
						.define('R', Items.REDSTONE_BLOCK)
						.define('G', Items.GOLD_BLOCK)
						.define('C', ModItems.DEEP_EXCAVATION_CORE)
						.pattern("RRR")
						.pattern("GCG")
						.pattern("RRR")
						.unlockedBy("has_deep_excavation_core", has(ModItems.DEEP_EXCAVATION_CORE))
						.save(output);

				shaped(RecipeCategory.MISC, ModItems.ADVANCED_EXCAVATION_CORE)
						.define('R', Items.REDSTONE_BLOCK)
						.define('D', Items.DIAMOND_BLOCK)
						.define('C', ModItems.WIDE_EXCAVATION_CORE)
						.pattern("RRR")
						.pattern("DCD")
						.pattern("RRR")
						.unlockedBy("has_wide_excavation_core", has(ModItems.WIDE_EXCAVATION_CORE))
						.save(output);

				buildEnhancementCore(ModItems.SILK_CORE, Items.STRING, Items.EMERALD);
				buildEnhancementCore(ModItems.COLLECTOR_CORE, Items.ENDER_PEARL, Items.LAPIS_LAZULI);
				buildEnhancementCore(ModItems.SMELTING_CORE, Items.MAGMA_CREAM, Items.BLAZE_POWDER);
				buildEnhancementCore(ModItems.FILTER_CORE, Items.QUARTZ, Items.AMETHYST_SHARD);
				buildEnhancementCore(ModItems.VOID_CORE, Items.ENDER_EYE, Items.ENDER_PEARL);
			}

			private void buildEnhancementCore(Item result, ItemLike outer, ItemLike inner) {
				shaped(RecipeCategory.MISC, result)
						.define('O', outer)
						.define('H', inner)
						.define('M', ModItems.CORE_HOUSING)
						.pattern("OHO")
						.pattern("HMH")
						.pattern("OHO")
						.unlockedBy("has_core_housing", has(ModItems.CORE_HOUSING))
						.save(output);
			}

			private void buildEnhancementWorkbench() {
				shaped(RecipeCategory.MISC, ModItems.ENHANCEMENT_WORKBENCH)
						.define('O', ItemTags.PLANKS)
						.define('H', Items.ANVIL)
						.define('N', Items.STONE_BRICKS)
						.pattern("OOO")
						.pattern("OHO")
						.pattern("NNN")
						.unlockedBy("has_anvil", has(Items.ANVIL))
						.save(output);
			}

			private void buildExcavators() {
				buildExcavators("stone", ModItems.STONE_EXCAVATOR, Items.STONE);
				buildExcavators("copper", ModItems.COPPER_EXCAVATOR, Items.COPPER_INGOT);
				buildExcavators("iron", ModItems.IRON_EXCAVATOR, Items.IRON_INGOT);
				buildExcavators("golden", ModItems.GOLDEN_EXCAVATOR, Items.GOLD_INGOT);
				buildExcavators("diamond", ModItems.DIAMOND_EXCAVATOR, Items.DIAMOND);
			}

			private void buildExcavators(String materialName, Item result, ItemLike material) {
				buildExcavator(materialName + "_excavator", result, material, ExcavationMode.BASIC, null);
				buildExcavator(materialName + "_deep_excavator", result, material, ExcavationMode.DEEP,
						ModItems.DEEP_EXCAVATION_CORE);
				buildExcavator(materialName + "_wide_excavator", result, material, ExcavationMode.WIDE,
						ModItems.WIDE_EXCAVATION_CORE);
				buildExcavator(materialName + "_advanced_excavator", result, material, ExcavationMode.ADVANCED,
						ModItems.ADVANCED_EXCAVATION_CORE);
			}

			private void buildExcavator(
					String name,
					Item result,
					ItemLike material,
					ExcavationMode mode,
					Item core
			) {
				Map<Character, Ingredient> ingredients = core == null
						? Map.of('M', Ingredient.of(material), 'S', Ingredient.of(Items.STICK))
						: Map.of(
								'M', Ingredient.of(material),
								'S', Ingredient.of(Items.STICK),
								'C', Ingredient.of(core)
						);
				ShapedRecipePattern pattern = ShapedRecipePattern.of(
						ingredients,
						core == null ? "MSM" : "MCM",
						" SM",
						" S "
				);
				DataComponentPatch resultComponents = DataComponentPatch.builder()
						.set(ExcavatorComponents.EXCAVATION_MODE, mode)
						.build();

				ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, JustExcavators.id(name));
				RecipeUnlockAdvancementBuilder advancement = new RecipeUnlockAdvancementBuilder();
				if (core == null) {
					advancement.unlockedBy("has_material", has(material));
				} else {
					advancement.unlockedBy("has_core", has(core));
				}

				output.accept(
						key,
						new ShapedRecipe(
								new Recipe.CommonInfo(true),
								new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.EQUIPMENT, ""),
								pattern,
								new ItemStackTemplate(result, resultComponents)
						),
						advancement.build(output, key, RecipeCategory.TOOLS)
				);
			}

			private void buildNetheriteUpgrade() {
				SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
						Ingredient.of(ModItems.DIAMOND_EXCAVATOR),
						Ingredient.of(Items.NETHERITE_INGOT),
						RecipeCategory.TOOLS,
						ModItems.NETHERITE_EXCAVATOR
				)
						.unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
						.save(output, JustExcavators.MOD_ID + ":netherite_excavator_smithing");
			}
		};
	}
}
