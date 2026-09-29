package net.hfstack.justexcavators.recipe;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryFixedCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;

public final class SilkCoreSmithingRecipe extends SimpleSmithingRecipe {
	public static final MapCodec<SilkCoreSmithingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
			Ingredient.CODEC.optionalFieldOf("template").forGetter(SilkCoreSmithingRecipe::templateIngredient),
			Ingredient.CODEC.fieldOf("base").forGetter(SilkCoreSmithingRecipe::baseIngredient),
			Ingredient.CODEC.optionalFieldOf("addition").forGetter(SilkCoreSmithingRecipe::additionIngredient),
			RegistryFixedCodec.create(Registries.ENCHANTMENT).fieldOf("enchantment")
					.forGetter(SilkCoreSmithingRecipe::silkTouch)
	).apply(instance, SilkCoreSmithingRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SilkCoreSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC,
			recipe -> recipe.commonInfo,
			Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
			SilkCoreSmithingRecipe::templateIngredient,
			Ingredient.CONTENTS_STREAM_CODEC,
			SilkCoreSmithingRecipe::baseIngredient,
			Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
			SilkCoreSmithingRecipe::additionIngredient,
			ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT),
			SilkCoreSmithingRecipe::silkTouch,
			SilkCoreSmithingRecipe::new
	);

	public static final RecipeSerializer<SilkCoreSmithingRecipe> SERIALIZER = new RecipeSerializer<>(
			MAP_CODEC,
			STREAM_CODEC
	);

	private final Optional<Ingredient> template;
	private final Ingredient base;
	private final Optional<Ingredient> addition;
	private final Holder<Enchantment> silkTouch;

	public SilkCoreSmithingRecipe(
			Recipe.CommonInfo commonInfo,
			Optional<Ingredient> template,
			Ingredient base,
			Optional<Ingredient> addition,
			Holder<Enchantment> silkTouch
	) {
		super(commonInfo);
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.silkTouch = silkTouch;
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		if (!Ingredient.testOptionalIngredient(template, input.template())
				|| !base.test(input.base())
				|| !Ingredient.testOptionalIngredient(addition, input.addition())) {
			return false;
		}

		ItemStack excavator = input.base();
		ExcavatorEnhancements enhancements = excavator.getOrDefault(
				ExcavatorComponents.ENHANCEMENTS,
				ExcavatorEnhancements.NONE
		);
		return SilkCoreApplicationPolicy.canApply(
				enhancements.silk(),
				hasEnchantment(excavator, Enchantments.SILK_TOUCH),
				hasEnchantment(excavator, Enchantments.FORTUNE)
		);
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		ItemStack result = input.base().copyWithCount(1);
		ExcavatorEnhancements enhancements = result.getOrDefault(
				ExcavatorComponents.ENHANCEMENTS,
				ExcavatorEnhancements.NONE
		);
		result.set(ExcavatorComponents.ENHANCEMENTS, enhancements.withSilk());
		result.enchant(silkTouch, 1);
		return result;
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return template;
	}

	@Override
	public Ingredient baseIngredient() {
		return base;
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return addition;
	}

	public Holder<Enchantment> silkTouch() {
		return silkTouch;
	}

	@Override
	public RecipeSerializer<SilkCoreSmithingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.createFromOptionals(List.of(template, Optional.of(base), addition));
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new SmithingRecipeDisplay(
				Ingredient.optionalIngredientToDisplay(template),
				base.display(),
				Ingredient.optionalIngredientToDisplay(addition),
				base.display(),
				new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
		));
	}

	private static boolean hasEnchantment(ItemStack stack, net.minecraft.resources.ResourceKey<Enchantment> enchantment) {
		return stack.getEnchantments().keySet().stream().anyMatch(holder -> holder.is(enchantment));
	}
}
