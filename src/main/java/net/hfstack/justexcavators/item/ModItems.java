package net.hfstack.justexcavators.item;

import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.block.ModBlocks;
import net.hfstack.justexcavators.enhancement.EnhancementType;

public final class ModItems {
	private static final Tier COPPER_TIER = new SimpleTier();
	public static final ExcavatorItem STONE_EXCAVATOR = registerExcavator("stone_excavator", Tiers.STONE);
	public static final ExcavatorItem COPPER_EXCAVATOR = registerExcavator("copper_excavator", COPPER_TIER);
	public static final ExcavatorItem IRON_EXCAVATOR = registerExcavator("iron_excavator", Tiers.IRON);
	public static final ExcavatorItem GOLDEN_EXCAVATOR = registerExcavator("golden_excavator", Tiers.GOLD);
	public static final ExcavatorItem DIAMOND_EXCAVATOR = registerExcavator("diamond_excavator", Tiers.DIAMOND);
	public static final ExcavatorItem NETHERITE_EXCAVATOR = registerExcavator("netherite_excavator", Tiers.NETHERITE, true);

	public static final Item DEEP_EXCAVATION_CORE = register("deep_excavation_core", Item::new);
	public static final Item WIDE_EXCAVATION_CORE = register("wide_excavation_core", Item::new);
	public static final Item ADVANCED_EXCAVATION_CORE = register("advanced_excavation_core", Item::new);
	public static final Item CORE_HOUSING = register("core_housing", Item::new);
	public static final Item SILK_CORE = registerEnhancementCore("silk_core", EnhancementType.SILK);
	public static final Item COLLECTOR_CORE = registerEnhancementCore("collector_core", EnhancementType.COLLECTOR);
	public static final Item SMELTING_CORE = registerEnhancementCore("smelting_core", EnhancementType.SMELTING);
	public static final Item FILTER_CORE = registerEnhancementCore("filter_core", EnhancementType.FILTER);
	public static final Item VOID_CORE = registerEnhancementCore("void_core", EnhancementType.VOID);
	public static final Item ENHANCEMENT_WORKBENCH = register(
			"enhancement_workbench",
			properties -> new BlockItem(ModBlocks.ENHANCEMENT_WORKBENCH, properties)
	);

	public static final List<ExcavatorItem> EXCAVATORS = List.of(
			STONE_EXCAVATOR,
			COPPER_EXCAVATOR,
			IRON_EXCAVATOR,
			GOLDEN_EXCAVATOR,
			DIAMOND_EXCAVATOR,
			NETHERITE_EXCAVATOR
	);

	public static final List<Item> CORES = List.of(
			DEEP_EXCAVATION_CORE,
			WIDE_EXCAVATION_CORE,
			ADVANCED_EXCAVATION_CORE,
			SILK_CORE,
			COLLECTOR_CORE,
			SMELTING_CORE,
			FILTER_CORE,
			VOID_CORE
	);

	private ModItems() {
	}

	public static void init() {
		// Loading this class performs the item registrations.
	}

	private static ExcavatorItem registerExcavator(String name, Tier material) {
		return registerExcavator(name, material, false);
	}

	private static ExcavatorItem registerExcavator(String name, Tier material, boolean fireResistant) {
		return register(name, properties -> new ExcavatorItem(fireResistant ? properties.fireResistant() : properties, material));
	}

	private static EnhancementCoreItem registerEnhancementCore(String name, EnhancementType type) {
		return register(name, properties -> new EnhancementCoreItem(properties, type));
	}

	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, JustExcavators.id(name));
		T item = factory.apply(new Item.Properties());
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	private static final class SimpleTier implements Tier {
		@Override public int getUses() { return 190; }
		@Override public float getSpeed() { return 5.0F; }
		@Override public float getAttackDamageBonus() { return 1.5F; }
		@Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_STONE_TOOL; }
		@Override public int getEnchantmentValue() { return 13; }
		@Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.COPPER_INGOT); }
	}
}
