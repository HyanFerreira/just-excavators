package net.hfstack.justexcavators.item;

import java.util.List;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import net.hfstack.justexcavators.JustExcavators;

public final class ModItems {
	public static final ExcavatorItem STONE_EXCAVATOR = registerExcavator("stone_excavator", ToolMaterial.STONE);
	public static final ExcavatorItem COPPER_EXCAVATOR = registerExcavator("copper_excavator", ToolMaterial.COPPER);
	public static final ExcavatorItem IRON_EXCAVATOR = registerExcavator("iron_excavator", ToolMaterial.IRON);
	public static final ExcavatorItem GOLDEN_EXCAVATOR = registerExcavator("golden_excavator", ToolMaterial.GOLD);
	public static final ExcavatorItem DIAMOND_EXCAVATOR = registerExcavator("diamond_excavator", ToolMaterial.DIAMOND);
	public static final ExcavatorItem NETHERITE_EXCAVATOR = registerExcavator("netherite_excavator", ToolMaterial.NETHERITE);

	public static final Item DEEP_EXCAVATION_CORE = register("deep_excavation_core", Item::new);
	public static final Item WIDE_EXCAVATION_CORE = register("wide_excavation_core", Item::new);
	public static final Item ADVANCED_EXCAVATION_CORE = register("advanced_excavation_core", Item::new);
	public static final Item SILK_CORE = register("silk_core", Item::new);
	public static final Item COLLECTOR_CORE = register("collector_core", Item::new);
	public static final Item SMELTING_CORE = register("smelting_core", Item::new);
	public static final Item FILTER_CORE = register("filter_core", Item::new);
	public static final Item VOID_CORE = register("void_core", Item::new);

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

	private static ExcavatorItem registerExcavator(String name, ToolMaterial material) {
		return register(name, properties -> new ExcavatorItem(properties, material));
	}

	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, JustExcavators.id(name));
		T item = factory.apply(new Item.Properties().setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}
}
