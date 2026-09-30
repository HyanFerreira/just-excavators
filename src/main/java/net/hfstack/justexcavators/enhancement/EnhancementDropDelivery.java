package net.hfstack.justexcavators.enhancement;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.world.item.ItemStack;

public final class EnhancementDropDelivery {
	private EnhancementDropDelivery() {
	}

	public static DeliveryResult deliver(
			ItemStack drop,
			boolean collector,
			boolean voiding,
			Function<ItemStack, ItemStack> inventoryInsert,
			Consumer<ItemStack> worldDrop
	) {
		Objects.requireNonNull(drop, "drop");
		Objects.requireNonNull(inventoryInsert, "inventoryInsert");
		Objects.requireNonNull(worldDrop, "worldDrop");
		if (drop.isEmpty()) {
			return DeliveryResult.EMPTY;
		}

		int originalCount = drop.getCount();
		if (voiding) {
			return new DeliveryResult(0, 0, originalCount);
		}
		if (!collector) {
			worldDrop.accept(drop);
			return new DeliveryResult(0, originalCount, 0);
		}

		ItemStack remainder = Objects.requireNonNull(
				inventoryInsert.apply(drop.copy()),
				"inventoryInsert returned null"
		);
		int remainderCount = remainder.isEmpty() ? 0 : remainder.getCount();
		if (remainderCount < 0 || remainderCount > originalCount) {
			throw new IllegalStateException("Inventory remainder count exceeds the original drop");
		}
		if (!remainder.isEmpty()) {
			worldDrop.accept(remainder);
		}
		return new DeliveryResult(originalCount - remainderCount, remainderCount, 0);
	}

	public record DeliveryResult(int insertedCount, int droppedCount, int discardedCount) {
		public static final DeliveryResult EMPTY = new DeliveryResult(0, 0, 0);

		public DeliveryResult {
			if (insertedCount < 0 || droppedCount < 0 || discardedCount < 0) {
				throw new IllegalArgumentException("Delivery counts cannot be negative");
			}
		}
	}
}
