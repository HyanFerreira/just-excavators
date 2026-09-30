package net.hfstack.justexcavators.enhancement;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.item.ModItems;

public final class EnhancementCoreCatalog {
	private static final EnhancementCoreIndex<Item> INDEX = EnhancementCoreIndex.create(Map.of(
			EnhancementType.SILK, ModItems.SILK_CORE,
			EnhancementType.COLLECTOR, ModItems.COLLECTOR_CORE,
			EnhancementType.SMELTING, ModItems.SMELTING_CORE,
			EnhancementType.FILTER, ModItems.FILTER_CORE,
			EnhancementType.VOID, ModItems.VOID_CORE
	));

	private EnhancementCoreCatalog() {
	}

	public static Optional<EnhancementType> typeOf(ItemStack stack) {
		Objects.requireNonNull(stack, "stack");
		return stack.isEmpty() ? Optional.empty() : typeOf(stack.getItem());
	}

	public static Optional<EnhancementType> typeOf(Item item) {
		return INDEX.typeOf(Objects.requireNonNull(item, "item"));
	}

	public static Item itemOf(EnhancementType type) {
		return INDEX.valueOf(type);
	}

	public static ItemStack stackOf(EnhancementType type) {
		return new ItemStack(itemOf(type));
	}
}
