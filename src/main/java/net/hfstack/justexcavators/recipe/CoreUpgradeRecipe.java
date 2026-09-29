package net.hfstack.justexcavators.recipe;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.level.Level;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;
import net.hfstack.justexcavators.item.ModItems;

public final class CoreUpgradeRecipe extends SimpleSmithingRecipe {
	public static final MapCodec<CoreUpgradeRecipe> MAP_CODEC = CoreUpgradeDefinition.MAP_CODEC.xmap(
			definition -> new CoreUpgradeRecipe(definition.mode()),
			recipe -> new CoreUpgradeDefinition(recipe.mode)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, CoreUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
			(buffer, recipe) -> buffer.writeEnum(recipe.mode),
			buffer -> new CoreUpgradeRecipe(buffer.readEnum(ExcavationMode.class))
	);

	private final ExcavationMode mode;

	public CoreUpgradeRecipe(ExcavationMode mode) {
		super(new Recipe.CommonInfo(true));
		this.mode = mode;
	}

	public ExcavationMode mode() {
		return mode;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		return matchesInput(input);
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		if (!matchesInput(input)) {
			return ItemStack.EMPTY;
		}

		ItemStack result = input.base().copyWithCount(1);
		result.set(ExcavatorComponents.EXCAVATION_MODE, mode);
		return result;
	}

	private boolean matchesInput(SmithingRecipeInput input) {
		return input.template().isEmpty()
				&& baseIngredient().test(input.base())
				&& additionIngredient().orElseThrow().test(input.addition())
				&& CoreUpgradeDefinition.canApply(currentMode(input.base()), mode);
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return Optional.empty();
	}

	@Override
	public Ingredient baseIngredient() {
		return Ingredient.of(ModItems.EXCAVATORS.stream());
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return Optional.of(Ingredient.of(coreFor(mode)));
	}

	@Override
	public RecipeSerializer<CoreUpgradeRecipe> getSerializer() {
		return ModRecipes.CORE_UPGRADE;
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(List.of(
				Optional.empty(),
				Optional.of(baseIngredient()),
				additionIngredient()
		));
	}

	@Override
	public List<RecipeDisplay> display() {
		return ModItems.EXCAVATORS.stream()
				.map(excavator -> (RecipeDisplay) createDisplay(excavator))
				.toList();
	}

	private SmithingRecipeDisplay createDisplay(Item excavator) {
		ItemStack result = new ItemStack(excavator);
		result.set(ExcavatorComponents.EXCAVATION_MODE, mode);

		return new SmithingRecipeDisplay(
				SlotDisplay.Empty.INSTANCE,
				new SlotDisplay.ItemSlotDisplay(excavator),
				new SlotDisplay.ItemSlotDisplay(coreFor(mode)),
				new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(result)),
				new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
		);
	}

	private static ExcavationMode currentMode(ItemStack stack) {
		return stack.getOrDefault(ExcavatorComponents.EXCAVATION_MODE, ExcavationMode.BASIC);
	}

	private static Item coreFor(ExcavationMode mode) {
		return switch (mode) {
			case BASIC -> ModItems.EXCAVATION_CORE;
			case DEEP -> ModItems.DEEP_EXCAVATION_CORE;
			case WIDE -> ModItems.WIDE_EXCAVATION_CORE;
		};
	}
}
