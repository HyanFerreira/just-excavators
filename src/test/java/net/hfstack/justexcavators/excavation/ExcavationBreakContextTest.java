package net.hfstack.justexcavators.excavation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.enhancement.ActiveEnhancements;

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

	private static ExcavationBreakContext context(boolean additional) {
		return new ExcavationBreakContext(null, null, ActiveEnhancements.EMPTY, null, null, additional);
	}
}
