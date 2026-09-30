package net.hfstack.justexcavators.enhancement;

import java.util.Optional;
import java.util.Set;

public final class EnhancementCompatibility {
	private static final Set<CorePair> ALLOWED_PAIRS = Set.of(
			CorePair.of(EnhancementType.SILK, EnhancementType.COLLECTOR),
			CorePair.of(EnhancementType.SILK, EnhancementType.SMELTING),
			CorePair.of(EnhancementType.SILK, EnhancementType.FILTER),
			CorePair.of(EnhancementType.COLLECTOR, EnhancementType.SMELTING),
			CorePair.of(EnhancementType.COLLECTOR, EnhancementType.FILTER),
			CorePair.of(EnhancementType.SMELTING, EnhancementType.FILTER),
			CorePair.of(EnhancementType.FILTER, EnhancementType.VOID)
	);

	private EnhancementCompatibility() {
	}

	public static boolean isValidPair(EnhancementType first, EnhancementType second) {
		return first != null && second != null && ALLOWED_PAIRS.contains(CorePair.of(first, second));
	}

	public static boolean isValidSlots(
			Optional<EnhancementType> first,
			Optional<EnhancementType> second
	) {
		if (first == null || second == null) {
			return false;
		}
		return first.isEmpty() || second.isEmpty() || isValidPair(first.orElseThrow(), second.orElseThrow());
	}

	public static boolean areEnchantmentsCompatible(
			Optional<EnhancementType> first,
			Optional<EnhancementType> second,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		if (!isValidSlots(first, second)) {
			return false;
		}

		boolean hasSilk = contains(first, second, EnhancementType.SILK);
		boolean hasVoid = contains(first, second, EnhancementType.VOID);
		return !(hasSilk && (hasSilkTouch || hasFortune))
				&& !(hasVoid && (hasSilkTouch || hasFortune));
	}

	private static boolean contains(
			Optional<EnhancementType> first,
			Optional<EnhancementType> second,
			EnhancementType expected
	) {
		return first.filter(expected::equals).isPresent() || second.filter(expected::equals).isPresent();
	}

	private record CorePair(EnhancementType first, EnhancementType second) {
		private static CorePair of(EnhancementType first, EnhancementType second) {
			return first.ordinal() <= second.ordinal()
					? new CorePair(first, second)
					: new CorePair(second, first);
		}
	}
}
