package net.hfstack.justexcavators.workbench;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.world.inventory.ContainerInput;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementCompatibility;
import net.hfstack.justexcavators.enhancement.EnhancementType;

public final class EnhancementWorkbenchTransactions {
	private EnhancementWorkbenchTransactions() {
	}

	public static Optional<CoreClickResult> click(
			ExcavatorEnhancements enhancements,
			int slot,
			Optional<EnhancementType> carriedType,
			int carriedCount,
			boolean canStoreOutgoing,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		Objects.requireNonNull(enhancements, "enhancements");
		Objects.requireNonNull(carriedType, "carriedType");
		if (!isSlot(slot) || carriedCount < 0 || (carriedCount == 0) != carriedType.isEmpty()) {
			return Optional.empty();
		}

		Optional<EnhancementType> installed = enhancements.slot(slot);
		if (carriedType.isEmpty()) {
			return installed.map(type -> new CoreClickResult(
					enhancements.withoutSlot(slot),
					0,
					Optional.of(type),
					Optional.empty()
			));
		}

		EnhancementType incoming = carriedType.orElseThrow();
		if (installed.filter(incoming::equals).isPresent() || !enhancements.canSet(slot, incoming)) {
			return Optional.empty();
		}

		ExcavatorEnhancements result = enhancements.withSlot(slot, incoming);
		if (!enchantmentsAllow(enhancements, result, hasSilkTouch, hasFortune)) {
			return Optional.empty();
		}

		if (installed.isEmpty()) {
			return Optional.of(new CoreClickResult(
					result,
					1,
					Optional.empty(),
					Optional.empty()
			));
		}

		EnhancementType outgoing = installed.orElseThrow();
		if (carriedCount == 1) {
			return Optional.of(new CoreClickResult(
					result,
					1,
					Optional.of(outgoing),
					Optional.empty()
			));
		}
		if (!canStoreOutgoing) {
			return Optional.empty();
		}
		return Optional.of(new CoreClickResult(
				result,
				1,
				Optional.empty(),
				Optional.of(outgoing)
		));
	}

	public static OptionalInt firstQuickInstallSlot(
			ExcavatorEnhancements enhancements,
			EnhancementType incoming,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		Objects.requireNonNull(enhancements, "enhancements");
		Objects.requireNonNull(incoming, "incoming");
		for (int slot = 0; slot < 2; slot++) {
			if (enhancements.slot(slot).isPresent() || !enhancements.canSet(slot, incoming)) {
				continue;
			}
			ExcavatorEnhancements result = enhancements.withSlot(slot, incoming);
			if (enchantmentsAllow(enhancements, result, hasSilkTouch, hasFortune)) {
				return OptionalInt.of(slot);
			}
		}
		return OptionalInt.empty();
	}

	public static boolean acceptsCoreInput(ContainerInput input) {
		return Objects.requireNonNull(input, "input") == ContainerInput.PICKUP;
	}

	private static boolean enchantmentsAllow(
			ExcavatorEnhancements current,
			ExcavatorEnhancements result,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		boolean hasNativeSilkTouch = hasSilkTouch && !current.has(EnhancementType.SILK);
		return EnhancementCompatibility.areEnchantmentsCompatible(
				result.slot(0),
				result.slot(1),
				hasNativeSilkTouch,
				hasFortune
		);
	}

	private static boolean isSlot(int slot) {
		return slot == 0 || slot == 1;
	}

	public record CoreClickResult(
			ExcavatorEnhancements enhancements,
			int consumedFromCursor,
			Optional<EnhancementType> cursorReturn,
			Optional<EnhancementType> inventoryReturn
	) {
		public CoreClickResult {
			Objects.requireNonNull(enhancements, "enhancements");
			Objects.requireNonNull(cursorReturn, "cursorReturn");
			Objects.requireNonNull(inventoryReturn, "inventoryReturn");
			if (consumedFromCursor < 0 || cursorReturn.isPresent() && inventoryReturn.isPresent()) {
				throw new IllegalArgumentException("Invalid Workbench transaction result");
			}
		}
	}
}
