package net.hfstack.justexcavators.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class LegacySilkMigrationTest {
	private static final DataComponentType<ExcavatorEnhancements> ENHANCEMENTS =
			DataComponentType.<ExcavatorEnhancements>builder().persistent(ExcavatorEnhancements.CODEC).build();
	private static Holder<Enchantment> silkTouch;
	private static Holder<Enchantment> unbreaking;

	@BeforeAll
	static void bootstrapRegistries() {
		HolderLookup.Provider registries = MinecraftTestBootstrap.registries();
		silkTouch = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
		unbreaking = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
	}

	@Test
	void removesOnlySyntheticSilkTouchAndCanonicalizesLegacyBooleanData() {
		ExcavatorEnhancements legacy = ExcavatorEnhancements.CODEC.parse(
				JsonOps.INSTANCE,
				JsonParser.parseString("true")
		).getOrThrow();
		ItemStack stack = new ItemStack(Items.DIAMOND_SHOVEL);
		stack.set(ENHANCEMENTS, legacy);
		stack.enchant(silkTouch, 1);
		stack.enchant(unbreaking, 2);

		assertTrue(LegacySilkMigration.migrate(stack, ENHANCEMENTS));

		assertEquals(0, stack.getEnchantments().getLevel(silkTouch));
		assertEquals(2, stack.getEnchantments().getLevel(unbreaking));
		assertFalse(stack.getOrDefault(ENHANCEMENTS, ExcavatorEnhancements.EMPTY)
				.legacySilkMigrationRequired());
	}

	@Test
	void leavesNewAdministrativeConflictUntouchedForInvalidFallback() {
		ExcavatorEnhancements current = ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK);
		ItemStack stack = new ItemStack(Items.DIAMOND_SHOVEL);
		stack.set(ENHANCEMENTS, current);
		stack.enchant(silkTouch, 1);

		assertFalse(LegacySilkMigration.migrate(stack, ENHANCEMENTS));
		assertEquals(1, stack.getEnchantments().getLevel(silkTouch));
		assertEquals(current, stack.get(ENHANCEMENTS));
	}
}
