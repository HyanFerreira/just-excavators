package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.enhancement.SmeltingDropProcessor.SmeltingResult;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class SmeltingDropProcessorTest {
	@BeforeAll
	static void bootstrapItems() {
		MinecraftTestBootstrap.registries();
	}

	@Test
	void transformsOneSandIntoOneGlass() {
		SmeltingResult result = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND), ignored -> Optional.of(new ItemStack(Items.GLASS))
		);
		assertTrue(result.transformed());
		assertStack(result, Items.GLASS, 1);
	}

	@Test
	void multipliesRecipeResultsPerInputUnit() {
		SmeltingResult clay = SmeltingDropProcessor.process(
				new ItemStack(Items.CLAY_BALL, 4), ignored -> Optional.of(new ItemStack(Items.BRICK))
		);
		assertStack(clay, Items.BRICK, 4);

		SmeltingResult doubled = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND, 3), ignored -> Optional.of(new ItemStack(Items.GLASS, 2))
		);
		assertStack(doubled, Items.GLASS, 6);
	}

	@Test
	void preservesComponentsFromTheRecipeResult() {
		ItemStack namedGlass = new ItemStack(Items.GLASS);
		namedGlass.set(DataComponents.CUSTOM_NAME, Component.literal("Refined"));
		SmeltingResult result = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND, 2), ignored -> Optional.of(namedGlass)
		);

		assertEquals(Component.literal("Refined"), result.outputs().getFirst().get(DataComponents.CUSTOM_NAME));
		assertEquals(2, result.outputs().getFirst().getCount());
	}

	@Test
	void leavesUnitsWithoutARecipeUnchanged() {
		SmeltingResult result = SmeltingDropProcessor.process(
				new ItemStack(Items.DIAMOND, 3), ignored -> Optional.empty()
		);
		assertFalse(result.transformed());
		assertStack(result, Items.DIAMOND, 3);
	}

	@Test
	void supportsMixedTransformedAndFallbackUnits() {
		AtomicInteger calls = new AtomicInteger();
		SmeltingResult result = SmeltingDropProcessor.process(
				new ItemStack(Items.SAND, 4), ignored -> calls.getAndIncrement() % 2 == 0
						? Optional.of(new ItemStack(Items.GLASS))
						: Optional.empty()
		);

		assertTrue(result.transformed());
		assertEquals(4, result.outputs().size());
		assertEquals(Items.GLASS, result.outputs().get(0).getItem());
		assertEquals(Items.SAND, result.outputs().get(1).getItem());
	}

	@Test
	void emptyInputProducesNoOutputsOrTransformation() {
		SmeltingResult result = SmeltingDropProcessor.process(
				ItemStack.EMPTY, ignored -> { throw new AssertionError("resolver called"); }
		);
		assertEquals(SmeltingResult.EMPTY, result);
	}

	private static void assertStack(SmeltingResult result, net.minecraft.world.item.Item item, int count) {
		assertEquals(1, result.outputs().size());
		assertEquals(item, result.outputs().getFirst().getItem());
		assertEquals(count, result.outputs().getFirst().getCount());
	}
}
