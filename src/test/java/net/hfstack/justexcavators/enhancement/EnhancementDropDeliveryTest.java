package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.hfstack.justexcavators.MinecraftTestBootstrap;
import net.hfstack.justexcavators.enhancement.EnhancementDropDelivery.DeliveryResult;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class EnhancementDropDeliveryTest {
	@BeforeAll
	static void bootstrapItems() {
		MinecraftTestBootstrap.registries();
	}

	@Test
	void normalDeliveryUsesTheVanillaWorldDrop() {
		ItemStack drop = stack(4);
		AtomicBoolean inventoryCalled = new AtomicBoolean();
		List<ItemStack> world = new ArrayList<>();
		DeliveryResult result = EnhancementDropDelivery.deliver(drop, false, false, incoming -> {
			inventoryCalled.set(true);
			return incoming;
		}, world::add);

		assertEquals(new DeliveryResult(0, 4, 0), result);
		assertFalse(inventoryCalled.get());
		assertSame(drop, world.get(0));
	}

	@Test
	void collectorCanInsertTheWholeDrop() {
		List<ItemStack> world = new ArrayList<>();
		DeliveryResult result = EnhancementDropDelivery.deliver(
				stack(5), true, false, incoming -> ItemStack.EMPTY, world::add
		);

		assertEquals(new DeliveryResult(5, 0, 0), result);
		assertTrue(world.isEmpty());
	}

	@Test
	void collectorDropsOnlyTheExactPartialRemainder() {
		List<ItemStack> world = new ArrayList<>();
		DeliveryResult result = EnhancementDropDelivery.deliver(
				stack(7), true, false, incoming -> incoming.copyWithCount(2), world::add
		);

		assertEquals(new DeliveryResult(5, 2, 0), result);
		assertEquals(1, world.size());
		assertEquals(2, world.get(0).getCount());
	}

	@Test
	void fullInventoryFallsBackToTheWholeWorldDrop() {
		List<ItemStack> world = new ArrayList<>();
		DeliveryResult result = EnhancementDropDelivery.deliver(
				stack(3), true, false, incoming -> incoming, world::add
		);

		assertEquals(new DeliveryResult(0, 3, 0), result);
		assertEquals(3, world.get(0).getCount());
	}

	@Test
	void voidTakesPrecedenceWithoutCallingEitherDestination() {
		AtomicBoolean inventoryCalled = new AtomicBoolean();
		AtomicBoolean worldCalled = new AtomicBoolean();
		DeliveryResult result = EnhancementDropDelivery.deliver(stack(6), true, true, incoming -> {
			inventoryCalled.set(true);
			return incoming;
		}, ignored -> worldCalled.set(true));

		assertEquals(new DeliveryResult(0, 0, 6), result);
		assertFalse(inventoryCalled.get());
		assertFalse(worldCalled.get());
	}

	@Test
	void emptyStacksDoNothing() {
		DeliveryResult result = EnhancementDropDelivery.deliver(
				ItemStack.EMPTY, true, false,
				incoming -> { throw new AssertionError("inventory called"); },
				incoming -> { throw new AssertionError("world called"); }
		);
		assertEquals(DeliveryResult.EMPTY, result);
	}

	@Test
	void everyNonVoidPathConservesTheOriginalCount() {
		List<DeliveryResult> results = List.of(
				EnhancementDropDelivery.deliver(stack(8), false, false, stack -> stack, stack -> { }),
				EnhancementDropDelivery.deliver(stack(8), true, false, stack -> ItemStack.EMPTY, stack -> { }),
				EnhancementDropDelivery.deliver(stack(8), true, false, stack -> stack.copyWithCount(3), stack -> { })
		);
		for (DeliveryResult result : results) {
			assertEquals(8, result.insertedCount() + result.droppedCount());
			assertEquals(0, result.discardedCount());
		}
	}

	private static ItemStack stack(int count) {
		return new ItemStack(Items.DIAMOND, count);
	}
}
