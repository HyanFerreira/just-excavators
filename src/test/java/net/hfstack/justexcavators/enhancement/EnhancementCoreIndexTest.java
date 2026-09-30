package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

final class EnhancementCoreIndexTest {
	@Test
	void roundTripsEveryEnhancementTypeByIdentity() {
		EnumMap<EnhancementType, Object> values = completeValues();
		EnhancementCoreIndex<Object> index = EnhancementCoreIndex.create(values);

		for (EnhancementType type : EnhancementType.values()) {
			Object value = values.get(type);
			assertEquals(value, index.valueOf(type));
			assertEquals(Optional.of(type), index.typeOf(value));
		}
	}

	@Test
	void rejectsUnknownAndEqualButNonidenticalValues() {
		EnhancementCoreIndex<String> index = EnhancementCoreIndex.create(Map.of(
				EnhancementType.SILK, new String("silk"),
				EnhancementType.COLLECTOR, new String("collector"),
				EnhancementType.SMELTING, new String("smelting"),
				EnhancementType.FILTER, new String("filter"),
				EnhancementType.VOID, new String("void")
		));

		assertEquals(Optional.empty(), index.typeOf(new String("silk")));
		assertEquals(Optional.empty(), index.typeOf("excavation-mode-core"));
	}

	@Test
	void failsFastForMissingOrDuplicateMappings() {
		EnumMap<EnhancementType, Object> missing = completeValues();
		missing.remove(EnhancementType.VOID);
		assertThrows(IllegalArgumentException.class, () -> EnhancementCoreIndex.create(missing));

		Object duplicate = new Object();
		EnumMap<EnhancementType, Object> duplicated = completeValues();
		duplicated.put(EnhancementType.SILK, duplicate);
		duplicated.put(EnhancementType.FILTER, duplicate);
		assertThrows(IllegalArgumentException.class, () -> EnhancementCoreIndex.create(duplicated));
	}

	private static EnumMap<EnhancementType, Object> completeValues() {
		EnumMap<EnhancementType, Object> values = new EnumMap<>(EnhancementType.class);
		for (EnhancementType type : EnhancementType.values()) {
			values.put(type, new Object());
		}
		return values;
	}
}
