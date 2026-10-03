package net.hfstack.justexcavators.enhancement;

import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public final class SmeltingRecipeResolver {
	private SmeltingRecipeResolver() {
	}

	public static Optional<ItemStack> smeltOne(ServerLevel level, ItemStack input) {
		if (input.isEmpty()) {
			return Optional.empty();
		}
		SingleRecipeInput recipeInput = new SingleRecipeInput(input.copyWithCount(1));
		return level.recipeAccess()
				.getRecipeFor(RecipeType.SMELTING, recipeInput, level)
				.map(holder -> holder.value().assemble(recipeInput, level.registryAccess()))
				.filter(result -> !result.isEmpty());
	}
}
