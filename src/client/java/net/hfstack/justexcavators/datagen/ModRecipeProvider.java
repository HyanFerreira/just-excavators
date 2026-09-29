package net.hfstack.justexcavators.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.item.ModItems;
import net.hfstack.justexcavators.recipe.CoreUpgradeRecipe;

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
				buildExcavators();
				buildProfileUpgrades();
				buildNetheriteUpgrade();
			}

			private void buildCores() {
				shaped(RecipeCategory.MISC, ModItems.EXCAVATION_CORE)
						.define('I', Items.IRON_INGOT)
						.define('R', Items.REDSTONE)
						.define('D', Items.DIAMOND)
						.pattern("IRI")
						.pattern("RDR")
						.pattern("IRI")
						.unlockedBy("has_diamond", has(Items.DIAMOND))
						.save(output);

				shaped(RecipeCategory.MISC, ModItems.DEEP_EXCAVATION_CORE)
						.define('I', Items.IRON_INGOT)
						.define('P', Items.PISTON)
						.define('C', ModItems.EXCAVATION_CORE)
						.pattern("IPI")
						.pattern("PCP")
						.pattern("IPI")
						.unlockedBy("has_excavation_core", has(ModItems.EXCAVATION_CORE))
						.save(output);

				shaped(RecipeCategory.MISC, ModItems.WIDE_EXCAVATION_CORE)
						.define('I', Items.IRON_INGOT)
						.define('S', Items.SLIME_BALL)
						.define('C', ModItems.EXCAVATION_CORE)
						.pattern("ISI")
						.pattern("SCS")
						.pattern("ISI")
						.unlockedBy("has_excavation_core", has(ModItems.EXCAVATION_CORE))
						.save(output);
			}

			private void buildExcavators() {
				buildExcavator(ModItems.IRON_EXCAVATOR, Items.IRON_INGOT);
				buildExcavator(ModItems.GOLDEN_EXCAVATOR, Items.GOLD_INGOT);
				buildExcavator(ModItems.DIAMOND_EXCAVATOR, Items.DIAMOND);
			}

			private void buildExcavator(Item result, ItemLike material) {
				shaped(RecipeCategory.TOOLS, result)
						.define('M', material)
						.define('C', ModItems.EXCAVATION_CORE)
						.define('S', Items.STICK)
						.pattern("MCM")
						.pattern("MSM")
						.pattern(" S ")
						.unlockedBy("has_excavation_core", has(ModItems.EXCAVATION_CORE))
						.save(output);
			}

			private void buildProfileUpgrades() {
				buildProfileUpgrade("basic_core_upgrade", ExcavationMode.BASIC, ModItems.EXCAVATION_CORE);
				buildProfileUpgrade("deep_core_upgrade", ExcavationMode.DEEP, ModItems.DEEP_EXCAVATION_CORE);
				buildProfileUpgrade("wide_core_upgrade", ExcavationMode.WIDE, ModItems.WIDE_EXCAVATION_CORE);
			}

			private void buildProfileUpgrade(String name, ExcavationMode mode, Item core) {
				ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, JustExcavators.id(name));
				RecipeUnlockAdvancementBuilder advancement = new RecipeUnlockAdvancementBuilder();
				advancement.unlockedBy("has_core", has(core));

				output.accept(
						key,
						new CoreUpgradeRecipe(mode),
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
