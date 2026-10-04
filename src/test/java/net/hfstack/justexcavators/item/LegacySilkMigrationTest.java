package net.hfstack.justexcavators.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class LegacySilkMigrationTest {
	private static Enchantment silkTouch;
	private static Enchantment unbreaking;

	@BeforeAll
	static void bootstrapRegistries() {
		MinecraftTestBootstrap.registries();
		silkTouch = Enchantments.SILK_TOUCH;
		unbreaking = Enchantments.UNBREAKING;
	}

	@Test
	void removesOnlySyntheticSilkTouchAndCanonicalizesLegacyBooleanData() {
		ExcavatorEnhancements legacy = ExcavatorEnhancements.CODEC.parse(
				JsonOps.INSTANCE,
				JsonParser.parseString("true")
		).getOrThrow(false, message -> {});
		ItemStack stack = new ItemStack(Items.DIAMOND_SHOVEL);
		ExcavatorComponents.setEnhancements(stack, legacy);
		stack.enchant(silkTouch, 1);
		stack.enchant(unbreaking, 2);

		assertTrue(LegacySilkMigration.migrate(stack));

		assertEquals(0, EnchantmentHelper.getItemEnchantmentLevel(silkTouch, stack));
		assertEquals(2, EnchantmentHelper.getItemEnchantmentLevel(unbreaking, stack));
		assertFalse(ExcavatorComponents.getEnhancements(stack)
				.legacySilkMigrationRequired());
	}

	@Test
	void leavesNewAdministrativeConflictUntouchedForInvalidFallback() {
		ExcavatorEnhancements current = ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK);
		ItemStack stack = new ItemStack(Items.DIAMOND_SHOVEL);
		ExcavatorComponents.setEnhancements(stack, current);
		stack.enchant(silkTouch, 1);

		assertFalse(LegacySilkMigration.migrate(stack));
		assertEquals(1, EnchantmentHelper.getItemEnchantmentLevel(silkTouch, stack));
		assertEquals(current, ExcavatorComponents.getEnhancements(stack));
	}
}
