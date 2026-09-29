package net.hfstack.justexcavators.excavation;

public final class ExcavationExecutionPolicy {
	private ExcavationExecutionPolicy() {
	}

	public static boolean canStart(StartFacts facts) {
		return facts.centralDestroyed()
				&& facts.centralEligible()
				&& !facts.sneaking()
				&& !facts.recursive();
	}

	public static boolean canContinue(boolean originalToolInMainHand, boolean toolUsable) {
		return originalToolInMainHand && toolUsable;
	}

	public static boolean shouldRetainHit(boolean activelyDestroyingTarget, boolean delayedTarget) {
		return activelyDestroyingTarget || delayedTarget;
	}

	public record StartFacts(
			boolean centralDestroyed,
			boolean centralEligible,
			boolean sneaking,
			boolean recursive
	) {
	}
}
