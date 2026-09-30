package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.excavation.ExcavationMode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class SilkLootToolTest {
	private static Holder<Enchantment> silkTouch;
	private static Holder<Enchantment> unbreaking;
	private static final DataComponentType<ExcavationMode> MODE_COMPONENT =
			DataComponentType.<ExcavationMode>builder().persistent(ExcavationMode.CODEC).build();
	private static final DataComponentType<ExcavatorEnhancements> ENHANCEMENTS_COMPONENT =
			DataComponentType.<ExcavatorEnhancements>builder().persistent(ExcavatorEnhancements.CODEC).build();

	@BeforeAll
	static void bootstrapRegistries() {
		HolderLookup.Provider registries = MinecraftTestBootstrap.registries();
		silkTouch = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
		unbreaking = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
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
		original.set(DataComponents.CUSTOM_NAME, Component.literal("Careful Digger"));
		original.set(MODE_COMPONENT, ExcavationMode.WIDE);
		original.set(ENHANCEMENTS_COMPONENT, installed);
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
		assertEquals(Component.literal("Careful Digger"), effective.get(DataComponents.CUSTOM_NAME));
		assertEquals(ExcavationMode.WIDE, effective.get(MODE_COMPONENT));
		assertEquals(installed, effective.get(ENHANCEMENTS_COMPONENT));
		assertEquals(2, effective.getEnchantments().getLevel(unbreaking));
		assertEquals(1, effective.getEnchantments().getLevel(silkTouch));
		assertFalse(original.getEnchantments().keySet().stream().anyMatch(holder -> holder.is(Enchantments.SILK_TOUCH)));
		assertTrue(effective.getEnchantments().keySet().stream().anyMatch(holder -> holder.is(Enchantments.SILK_TOUCH)));
	}
}
