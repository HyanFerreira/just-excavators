package net.hfstack.justexcavators.enhancement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.item.ItemStack;

public final class SmeltingDropProcessor {
	private SmeltingDropProcessor() {
	}

	public static SmeltingResult process(
			ItemStack input,
			Function<ItemStack, Optional<ItemStack>> smeltOne
	) {
		Objects.requireNonNull(input, "input");
		Objects.requireNonNull(smeltOne, "smeltOne");
		if (input.isEmpty()) {
			return SmeltingResult.EMPTY;
		}

		List<ItemStack> outputs = new ArrayList<>();
		boolean transformed = false;
		for (int unit = 0; unit < input.getCount(); unit++) {
			Optional<ItemStack> resolved = Objects.requireNonNull(
					smeltOne.apply(input.copyWithCount(1)),
					"smeltOne returned null"
			);
			ItemStack output = resolved.filter(stack -> !stack.isEmpty())
					.map(ItemStack::copy)
					.orElseGet(() -> input.copyWithCount(1));
			transformed |= resolved.filter(stack -> !stack.isEmpty()).isPresent();
			append(outputs, output);
		}
		return new SmeltingResult(List.copyOf(outputs), transformed);
	}

	private static void append(List<ItemStack> outputs, ItemStack incoming) {
		int remaining = incoming.getCount();
		if (!outputs.isEmpty()) {
			ItemStack previous = outputs.get(outputs.size() - 1);
			if (ItemStack.isSameItemSameTags(previous, incoming)) {
				int moved = Math.min(remaining, previous.getMaxStackSize() - previous.getCount());
				previous.grow(moved);
				remaining -= moved;
			}
		}
		while (remaining > 0) {
			int count = Math.min(remaining, incoming.getMaxStackSize());
			outputs.add(incoming.copyWithCount(count));
			remaining -= count;
		}
	}

	public record SmeltingResult(List<ItemStack> outputs, boolean transformed) {
		public static final SmeltingResult EMPTY = new SmeltingResult(List.of(), false);

		public SmeltingResult {
			outputs = List.copyOf(outputs);
		}
	}
}
