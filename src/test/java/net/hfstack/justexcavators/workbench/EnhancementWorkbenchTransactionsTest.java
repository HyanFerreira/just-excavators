package net.hfstack.justexcavators.workbench;

import static net.hfstack.justexcavators.enhancement.EnhancementType.COLLECTOR;
import static net.hfstack.justexcavators.enhancement.EnhancementType.FILTER;
import static net.hfstack.justexcavators.enhancement.EnhancementType.SILK;
import static net.hfstack.justexcavators.enhancement.EnhancementType.SMELTING;
import static net.hfstack.justexcavators.enhancement.EnhancementType.VOID;
import static net.minecraft.world.inventory.ClickType.PICKUP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

import net.minecraft.world.inventory.ClickType;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.workbench.EnhancementWorkbenchTransactions.CoreClickResult;

final class EnhancementWorkbenchTransactionsTest {
	@Test
	void installsIntoTheExactEmptySlotWithoutCompacting() {
		CoreClickResult second = click(ExcavatorEnhancements.EMPTY, 1, FILTER, 12, true, false, false);
		assertEquals(Optional.empty(), second.enhancements().slot(0));
		assertEquals(Optional.of(FILTER), second.enhancements().slot(1));
		assertEquals(1, second.consumedFromCursor());
		assertEquals(Optional.empty(), second.cursorReturn());
		assertEquals(Optional.empty(), second.inventoryReturn());

		CoreClickResult first = click(second.enhancements(), 0, SILK, 1, true, false, false);
		assertEquals(Optional.of(SILK), first.enhancements().slot(0));
		assertEquals(Optional.of(FILTER), first.enhancements().slot(1));
	}

	@Test
	void removesAnInstalledCoreToTheEmptyCursorAndPreservesTheGap() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY
				.withSlot(0, SILK)
				.withSlot(1, FILTER);

		CoreClickResult result = remove(installed, 0, true, false);
		assertEquals(Optional.empty(), result.enhancements().slot(0));
		assertEquals(Optional.of(FILTER), result.enhancements().slot(1));
		assertEquals(Optional.of(SILK), result.cursorReturn());
		assertEquals(0, result.consumedFromCursor());
	}

	@Test
	void replacesToCursorWhenExactlyOneCoreIsCarried() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY.withSlot(0, COLLECTOR);
		CoreClickResult result = click(installed, 0, SMELTING, 1, false, false, false);

		assertEquals(Optional.of(SMELTING), result.enhancements().slot(0));
		assertEquals(Optional.of(COLLECTOR), result.cursorReturn());
		assertEquals(Optional.empty(), result.inventoryReturn());
	}

	@Test
	void stackedReplacementUsesInventoryOrRejectsAtomicallyWhenFull() {
		ExcavatorEnhancements installed = ExcavatorEnhancements.EMPTY.withSlot(0, COLLECTOR);
		CoreClickResult accepted = click(installed, 0, SMELTING, 8, true, false, false);
		assertEquals(Optional.of(SMELTING), accepted.enhancements().slot(0));
		assertEquals(Optional.empty(), accepted.cursorReturn());
		assertEquals(Optional.of(COLLECTOR), accepted.inventoryReturn());

		assertTrue(EnhancementWorkbenchTransactions.click(
				installed, 0, Optional.of(SMELTING), 8, false, false, false
		).isEmpty());
	}

	@Test
	void rejectsDuplicateIncompatibleAndMalformedClicks() {
		ExcavatorEnhancements silkFilter = ExcavatorEnhancements.EMPTY
				.withSlot(0, SILK)
				.withSlot(1, FILTER);
		assertRejected(silkFilter, 1, SILK, 1);
		assertRejected(silkFilter, 1, VOID, 1);
		assertRejected(silkFilter, -1, COLLECTOR, 1);
		assertRejected(silkFilter, 2, COLLECTOR, 1);
		assertTrue(EnhancementWorkbenchTransactions.click(
				silkFilter, 0, Optional.empty(), 1, true, false, false
		).isEmpty());
		assertTrue(EnhancementWorkbenchTransactions.click(
				silkFilter, 0, Optional.of(COLLECTOR), 0, true, false, false
		).isEmpty());
	}

	@Test
	void quickInstallSelectsTheFirstCompatibleEmptySlotWithoutReplacement() {
		assertEquals(OptionalInt.of(0), quick(ExcavatorEnhancements.EMPTY, FILTER, false, false));

		ExcavatorEnhancements firstOccupied = ExcavatorEnhancements.EMPTY.withSlot(0, COLLECTOR);
		assertEquals(OptionalInt.of(1), quick(firstOccupied, FILTER, false, false));

		ExcavatorEnhancements secondOccupied = ExcavatorEnhancements.EMPTY.withSlot(1, FILTER);
		assertEquals(OptionalInt.of(0), quick(secondOccupied, SILK, false, false));

		ExcavatorEnhancements full = firstOccupied.withSlot(1, FILTER);
		assertEquals(OptionalInt.empty(), quick(full, SMELTING, false, false));
		ExcavatorEnhancements incompatibleSecond = ExcavatorEnhancements.EMPTY.withSlot(1, SILK);
		assertEquals(OptionalInt.empty(), quick(incompatibleSecond, VOID, false, false));
	}

	@Test
	void enforcesNativeEnchantmentsButIgnoresTransitionalSyntheticSilkTouch() {
		assertEquals(OptionalInt.empty(), quick(ExcavatorEnhancements.EMPTY, SILK, true, false));
		assertEquals(OptionalInt.empty(), quick(ExcavatorEnhancements.EMPTY, VOID, true, false));
		assertEquals(OptionalInt.empty(), quick(ExcavatorEnhancements.EMPTY, SILK, false, true));
		assertEquals(OptionalInt.empty(), quick(ExcavatorEnhancements.EMPTY, VOID, false, true));

		ExcavatorEnhancements transitionalSilk = ExcavatorEnhancements.EMPTY.withSlot(0, SILK);
		assertEquals(OptionalInt.of(1), quick(transitionalSilk, FILTER, true, false));
		CoreClickResult replacement = click(
				transitionalSilk, 0, COLLECTOR, 1, true, true, false
		);
		assertEquals(Optional.of(COLLECTOR), replacement.enhancements().slot(0));
	}

	@Test
	void onlyNormalPickupMayMutateAProjectedCoreSlotDirectly() {
		for (ClickType input : ClickType.values()) {
			assertEquals(input == PICKUP, EnhancementWorkbenchTransactions.acceptsCoreInput(input));
		}
		assertFalse(EnhancementWorkbenchTransactions.acceptsCoreInput(ClickType.QUICK_MOVE));
	}

	private static CoreClickResult click(
			ExcavatorEnhancements enhancements,
			int slot,
			EnhancementType carried,
			int count,
			boolean canStoreOutgoing,
			boolean silkTouch,
			boolean fortune
	) {
		return EnhancementWorkbenchTransactions.click(
				enhancements,
				slot,
				Optional.of(carried),
				count,
				canStoreOutgoing,
				silkTouch,
				fortune
		).orElseThrow();
	}

	private static CoreClickResult remove(
			ExcavatorEnhancements enhancements,
			int slot,
			boolean silkTouch,
			boolean fortune
	) {
		return EnhancementWorkbenchTransactions.click(
				enhancements,
				slot,
				Optional.empty(),
				0,
				true,
				silkTouch,
				fortune
		).orElseThrow();
	}

	private static OptionalInt quick(
			ExcavatorEnhancements enhancements,
			EnhancementType incoming,
			boolean silkTouch,
			boolean fortune
	) {
		return EnhancementWorkbenchTransactions.firstQuickInstallSlot(
				enhancements, incoming, silkTouch, fortune
		);
	}

	private static void assertRejected(
			ExcavatorEnhancements enhancements,
			int slot,
			EnhancementType incoming,
			int count
	) {
		assertTrue(EnhancementWorkbenchTransactions.click(
				enhancements,
				slot,
				Optional.of(incoming),
				count,
				true,
				false,
				false
		).isEmpty());
	}
}
