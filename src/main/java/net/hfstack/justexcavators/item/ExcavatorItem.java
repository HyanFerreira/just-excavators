package net.hfstack.justexcavators.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.excavation.ExcavationMode;

public final class ExcavatorItem extends ShovelItem {
	private static final int DURABILITY_MULTIPLIER = 3;

	public ExcavatorItem(Item.Properties properties, Tier material) {
		super(withScaledDurability(material), 1.5F, -3.0F, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		LegacySilkMigration.migrate(stack);
	}

	@Override
	public void appendHoverText(
			ItemStack stack,
			@Nullable Level level,
			List<Component> text,
			TooltipFlag flag
	) {
		ExcavationMode mode = ExcavatorComponents.getMode(stack);

		text.add(Component.translatable(
				"tooltip.justexcavators.mode",
				Component.translatable(mode.translationKey())
		).withStyle(ChatFormatting.GRAY));
		text.add(Component.translatable(
				"tooltip.justexcavators.area",
				mode.width(),
				mode.height(),
				mode.depth()
		).withStyle(ChatFormatting.GRAY));
		ExcavatorEnhancements enhancements = ExcavatorComponents.getEnhancements(stack);
		text.addAll(EnhancementTooltipContent.lines(enhancements));
		text.add(Component.translatable("tooltip.justexcavators.precision")
				.withStyle(ChatFormatting.DARK_GRAY));
	}

	private static Tier withScaledDurability(Tier material) {
		return new ScaledTier(material);
	}

	private record ScaledTier(Tier delegate) implements Tier {
		@Override public int getLevel() { return delegate.getLevel(); }
		@Override public int getUses() { return delegate.getUses() * DURABILITY_MULTIPLIER; }
		@Override public float getSpeed() { return delegate.getSpeed(); }
		@Override public float getAttackDamageBonus() { return delegate.getAttackDamageBonus(); }
		@Override public int getEnchantmentValue() { return delegate.getEnchantmentValue(); }
		@Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() { return delegate.getRepairIngredient(); }
	}
}
