package net.hfstack.justexcavators.excavation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.enhancement.ActiveEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;

final class ExcavationBreakContextTest {
	@Test
	void exposesCurrentContextAndRemovesItAfterClose() {
		ExcavationBreakContext context = context(false);
		try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(context)) {
			assertSame(context, ExcavationBreakContext.current().orElseThrow());
		}
		assertTrue(ExcavationBreakContext.current().isEmpty());
	}

	@Test
	void nestedScopesRestoreTheirParentAndRequireLifoClose() {
		ExcavationBreakContext parent = context(false);
		ExcavationBreakContext child = context(true);
		ExcavationBreakContext.Scope outer = ExcavationBreakContext.open(parent);
		ExcavationBreakContext.Scope inner = ExcavationBreakContext.open(child);
		assertThrows(IllegalStateException.class, outer::close);
		assertSame(child, ExcavationBreakContext.current().orElseThrow());
		inner.close();
		assertSame(parent, ExcavationBreakContext.current().orElseThrow());
		outer.close();
	}

	@Test
	void scopesForTheSameContextStillRequireLifoOwnership() {
		ExcavationBreakContext context = context(false);
		ExcavationBreakContext.Scope outer = ExcavationBreakContext.open(context);
		ExcavationBreakContext.Scope inner = ExcavationBreakContext.open(context);
		assertThrows(IllegalStateException.class, outer::close);
		inner.close();
		outer.close();
		assertTrue(ExcavationBreakContext.current().isEmpty());
	}

	@Test
	void tryWithResourcesDoesNotLeakWhenActionThrows() {
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(context(false))) {
				throw new RuntimeException("expected");
			}
		});
		assertEquals("expected", thrown.getMessage());
		assertTrue(ExcavationBreakContext.current().isEmpty());
	}

	@Test
	void collectorAndVoidAreVisibleForCentralAndAdditionalBreaks() {
		for (EnhancementType type : new EnhancementType[] {
				EnhancementType.COLLECTOR, EnhancementType.VOID
		}) {
			ActiveEnhancements active = ActiveEnhancements.resolve(
					ExcavatorEnhancements.EMPTY.withSlot(0, type), false, false
			);
			for (boolean additional : new boolean[] { false, true }) {
				try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(
						context(additional, active)
				)) {
					ExcavationBreakContext current = ExcavationBreakContext.current().orElseThrow();
					assertEquals(additional, current.additional());
					assertTrue(current.enhancements().has(type));
				}
			}
		}
	}

	@Test
	void sneakingPreventsOnlyAoeAndDoesNotRemoveTheCentralContext() {
		ActiveEnhancements collector = ActiveEnhancements.resolve(
				ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.COLLECTOR), false, false
		);
		assertFalse(ExcavationExecutionPolicy.canStart(
				new ExcavationExecutionPolicy.StartFacts(true, true, true, false)
		));
		try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(context(false, collector))) {
			assertTrue(ExcavationBreakContext.current().orElseThrow()
					.enhancements().has(EnhancementType.COLLECTOR));
		}
	}

	@Test
	void transformationMarksBelongOnlyToTheirBreakScope() {
		ExcavationBreakContext outerContext = context(false);
		ExcavationBreakContext innerContext = context(true);
		try (ExcavationBreakContext.Scope ignored = ExcavationBreakContext.open(outerContext)) {
			outerContext.markDropTransformed();
			assertTrue(outerContext.transformedAnyDrop());
			try (ExcavationBreakContext.Scope nested = ExcavationBreakContext.open(innerContext)) {
				assertFalse(innerContext.transformedAnyDrop());
				innerContext.markDropTransformed();
			}
			assertTrue(outerContext.transformedAnyDrop());
		}
	}

	private static ExcavationBreakContext context(boolean additional) {
		return context(additional, ActiveEnhancements.EMPTY);
	}

	private static ExcavationBreakContext context(boolean additional, ActiveEnhancements enhancements) {
		return new ExcavationBreakContext(null, null, enhancements, null, null, additional);
	}
}
