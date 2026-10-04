package net.hfstack.justexcavators.recipe;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;

public final class ModeShapedRecipe implements CraftingRecipe {
	private final ShapedRecipe delegate;
	private final ExcavationMode mode;

	private ModeShapedRecipe(ShapedRecipe delegate, ExcavationMode mode) {
		this.delegate = delegate;
		this.mode = mode;
	}

	private ItemStack result(RegistryAccess registries) {
		ItemStack result = delegate.getResultItem(registries).copy();
		ExcavatorComponents.setMode(result, mode);
		return result;
	}

	@Override public boolean matches(CraftingContainer container, Level level) { return delegate.matches(container, level); }
	@Override public ItemStack assemble(CraftingContainer container, RegistryAccess registries) { return result(registries); }
	@Override public boolean canCraftInDimensions(int width, int height) { return delegate.canCraftInDimensions(width, height); }
	@Override public ItemStack getResultItem(RegistryAccess registries) { return result(registries); }
	@Override public NonNullList<Ingredient> getIngredients() { return delegate.getIngredients(); }
	@Override public boolean showNotification() { return delegate.showNotification(); }
	@Override public String getGroup() { return delegate.getGroup(); }
	@Override public ResourceLocation getId() { return delegate.getId(); }
	@Override public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.MODE_SHAPED; }
	@Override public CraftingBookCategory category() { return delegate.category(); }

	public static final class Serializer implements RecipeSerializer<ModeShapedRecipe> {
		private final ShapedRecipe.Serializer shaped = new ShapedRecipe.Serializer();

		@Override
		public ModeShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
			ExcavationMode mode = ExcavationMode.fromSerializedName(json.get("excavation_mode").getAsString())
					.orElseThrow(() -> new IllegalArgumentException("Unknown excavation mode"));
			return new ModeShapedRecipe(shaped.fromJson(id, json), mode);
		}

		@Override
		public ModeShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
			ExcavationMode mode = buffer.readEnum(ExcavationMode.class);
			return new ModeShapedRecipe(shaped.fromNetwork(id, buffer), mode);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, ModeShapedRecipe recipe) {
			buffer.writeEnum(recipe.mode);
			shaped.toNetwork(buffer, recipe.delegate);
		}
	}
}
