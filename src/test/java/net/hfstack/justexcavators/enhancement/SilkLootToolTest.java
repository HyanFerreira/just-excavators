package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.excavation.ExcavationMode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class SilkLootToolTest {
	private static Enchantment silkTouch;
	private static Enchantment unbreaking;

	@BeforeAll
	static void bootstrapRegistries() {
		MinecraftTestBootstrap.registries();
		silkTouch = Enchantments.SILK_TOUCH;
		unbreaking = Enchantments.UNBREAKING;
	}

	@Test
	void inactiveSilkReturnsTheOriginalStackByIdentity() {
		ItemStack original = new ItemStack(Items.DIAMOND_SHOVEL);

		ItemStack effective = SilkLootTool.forLoot(
				original,
				ActiveEnhancements.resolve(ExcavatorEnhancements.EMPTY, false, false),
				silkTouch
		);

		assertSame(original, effective);
	}

	@Test
	void activeSilkCreatesAnIsolatedCountOneCopyAndPreservesComponents() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.FILTER);
		ItemStack original = new ItemStack(Items.DIAMOND_SHOVEL);
		original.setCount(3);
		original.setDamageValue(17);
		original.setHoverName(Component.literal("Careful Digger"));
		ExcavatorComponents.setMode(original, ExcavationMode.WIDE);
		ExcavatorComponents.setEnhancements(original, installed);
		original.enchant(unbreaking, 2);

		ItemStack effective = SilkLootTool.forLoot(
				original,
				ActiveEnhancements.resolve(installed, false, false),
				silkTouch
		);

		assertNotSame(original, effective);
		assertEquals(1, effective.getCount());
		assertEquals(3, original.getCount());
		assertEquals(17, effective.getDamageValue());
		assertEquals(Component.literal("Careful Digger"), effective.getHoverName());
		assertEquals(ExcavationMode.WIDE, ExcavatorComponents.getMode(effective));
		assertEquals(installed, ExcavatorComponents.getEnhancements(effective));
		assertEquals(2, EnchantmentHelper.getItemEnchantmentLevel(unbreaking, effective));
		assertEquals(1, EnchantmentHelper.getItemEnchantmentLevel(silkTouch, effective));
		assertEquals(0, EnchantmentHelper.getItemEnchantmentLevel(silkTouch, original));
		assertTrue(EnchantmentHelper.getItemEnchantmentLevel(silkTouch, effective) > 0);
	}
}
