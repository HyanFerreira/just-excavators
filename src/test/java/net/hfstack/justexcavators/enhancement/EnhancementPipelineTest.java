package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementDropDelivery.DeliveryResult;
import net.hfstack.justexcavators.enhancement.SmeltingDropProcessor.SmeltingResult;
import net.hfstack.justexcavators.excavation.ExcavationExecutionPolicy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class EnhancementPipelineTest {
	private static Enchantment silkTouch;

	@BeforeAll
	static void bootstrap() {
		MinecraftTestBootstrap.registries();
		silkTouch = Enchantments.SILK_TOUCH;
	}

	@Test
	void silkLootFlowsIntoSmelting() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.SMELTING);
		ActiveEnhancements active = ActiveEnhancements.resolve(installed, false, false);
		ItemStack original = new ItemStack(Items.DIAMOND_SHOVEL);
		ItemStack effective = SilkLootTool.forLoot(original, active, silkTouch);
		assertTrue(EnchantmentHelper.getItemEnchantmentLevel(silkTouch, effective) > 0);

		ItemStack vanillaLoot = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, effective) > 0
				? new ItemStack(Items.IRON_ORE)
				: new ItemStack(Items.RAW_IRON);
		SmeltingResult result = SmeltingDropProcessor.process(
				vanillaLoot,
				input -> input.is(Items.IRON_ORE)
						? Optional.of(new ItemStack(Items.IRON_INGOT))
						: Optional.empty()
		);
		assertEquals(Items.IRON_INGOT, result.outputs().get(0).getItem());
		assertTrue(result.transformed());
	}

	@Test
	void fortuneSizedLootFlowsIntoSmelting() {
		ActiveEnhancements active = ActiveEnhancements.resolve(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SMELTING), false, true
		);
		assertTrue(active.valid());
		SmeltingResult result = SmeltingDropProcessor.process(
				new ItemStack(Items.RAW_GOLD, 3),
				ignored -> Optional.of(new ItemStack(Items.GOLD_INGOT))
		);
		assertEquals(3, result.outputs().get(0).getCount());
	}

	@Test
	void smeltingOutputsFlowIntoCollectorWithExactOverflow() {
		SmeltingResult smelted = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND, 5),
				ignored -> Optional.of(new ItemStack(Items.GLASS))
		);
		List<ItemStack> world = new ArrayList<>();
		DeliveryResult delivered = EnhancementDropDelivery.deliver(
				smelted.outputs().get(0), true, false,
				incoming -> incoming.copyWithCount(2), world::add
		);
		assertEquals(new DeliveryResult(3, 2, 0), delivered);
		assertEquals(2, world.get(0).getCount());
	}

	@Test
	void filterRunsBeforeVoidAndVoidSuppressesDropsAndExperience() {
		ActiveEnhancements active = ActiveEnhancements.resolve(
				ExcavatorEnhancements.EMPTY
						.withSlot(0, EnhancementType.FILTER)
						.withSlot(1, EnhancementType.VOID),
				false, false
		);
		assertFalse(FilterCorePolicy.shouldProcess(
				true, active, Blocks.DIRT, Blocks.SAND
		));
		assertTrue(FilterCorePolicy.shouldProcess(
				true, active, Blocks.DIRT, Blocks.DIRT
		));
		DeliveryResult result = EnhancementDropDelivery.deliver(
				new ItemStack(Items.DIRT), false, true, stack -> stack, stack -> { }
		);
		assertEquals(1, result.discardedCount());
		assertFalse(EnhancementDropDelivery.shouldSpawnAfterBreak(true));
	}

	@Test
	void invalidAdministrativeCombinationFallsBackToVanillaEffects() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK);
		ActiveEnhancements invalid = ActiveEnhancements.resolve(installed, false, true);
		ItemStack tool = new ItemStack(Items.DIAMOND_SHOVEL);
		assertFalse(invalid.valid());
		assertSame(tool, SilkLootTool.forLoot(tool, invalid, silkTouch));
		assertTrue(FilterCorePolicy.shouldProcess(true, invalid, Blocks.DIRT, Blocks.SAND));
		List<ItemStack> world = new ArrayList<>();
		EnhancementDropDelivery.deliver(
				new ItemStack(Items.DIRT), false, false, stack -> stack, world::add
		);
		assertEquals(1, world.size());
		assertTrue(EnhancementDropDelivery.shouldSpawnAfterBreak(false));
	}

	@Test
	void sneakingStopsOnlyAoeWhileCentralEnhancementsStillCompose() {
		assertFalse(ExcavationExecutionPolicy.canStart(
				new ExcavationExecutionPolicy.StartFacts(true, true, true, false)
		));
		ActiveEnhancements filter = ActiveEnhancements.resolve(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.FILTER), false, false
		);
		assertTrue(FilterCorePolicy.shouldProcess(false, filter, Blocks.DIRT, Blocks.SAND));
	}

	@Test
	void durabilityIsChargedOncePerTransformedBlock() {
		SmeltingResult first = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND, 8), ignored -> Optional.of(new ItemStack(Items.GLASS))
		);
		SmeltingResult second = SmeltingDropProcessor.process(
				new ItemStack(Items.DIAMOND, 8), ignored -> Optional.empty()
		);
		assertEquals(1, EnhancementDurability.extraPotentialDamage(true, first.transformed()));
		assertEquals(0, EnhancementDurability.extraPotentialDamage(true, second.transformed()));
	}
}
