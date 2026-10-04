package net.hfstack.justexcavators.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.Test;

final class ExcavatorEnhancementsTest {
	@Test
	void emptyHasTwoAddressableEmptySlots() {
		assertEquals(Optional.empty(), ExcavatorEnhancements.EMPTY.slot(0));
		assertEquals(Optional.empty(), ExcavatorEnhancements.EMPTY.slot(1));
		assertEquals(0, ExcavatorEnhancements.EMPTY.size());
	}

	@Test
	void preservesAnOccupiedSecondSlotWhenFirstSlotIsEmpty() {
		ExcavatorEnhancements enhancements = ExcavatorEnhancements.EMPTY
				.withSlot(1, EnhancementType.FILTER);

		assertEquals(Optional.empty(), enhancements.slot(0));
		assertEquals(Optional.of(EnhancementType.FILTER), enhancements.slot(1));
		assertEquals(1, enhancements.size());
	}

	@Test
	void installsAndRemovesWithoutCompactingSlots() {
		ExcavatorEnhancements enhancements = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.SMELTING)
				.withoutSlot(0);

		assertEquals(Optional.empty(), enhancements.slot(0));
		assertEquals(Optional.of(EnhancementType.SMELTING), enhancements.slot(1));
		assertEquals(1, enhancements.size());
	}

	@Test
	void validatesDuplicateAndIncompatibleMutationsBeforeApplyingThem() {
		ExcavatorEnhancements silk = ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK);
		assertFalse(silk.canSet(1, EnhancementType.SILK));
		assertThrows(IllegalArgumentException.class, () -> silk.withSlot(1, EnhancementType.SILK));

		ExcavatorEnhancements collector = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.COLLECTOR);
		assertFalse(collector.canSet(1, EnhancementType.VOID));
		assertThrows(IllegalArgumentException.class, () -> collector.withSlot(1, EnhancementType.VOID));

		ExcavatorEnhancements filter = ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.FILTER);
		assertTrue(filter.canSet(1, EnhancementType.VOID));
		assertEquals(Optional.of(EnhancementType.VOID), filter
				.withSlot(1, EnhancementType.VOID)
				.slot(1));
	}

	@Test
	void rejectsInvalidSlotIndexesAndNullOptionals() {
		assertThrows(IllegalArgumentException.class, () -> ExcavatorEnhancements.EMPTY.slot(-1));
		assertThrows(IllegalArgumentException.class, () -> ExcavatorEnhancements.EMPTY.slot(2));
		assertThrows(IllegalArgumentException.class,
				() -> ExcavatorEnhancements.EMPTY.withSlot(-1, EnhancementType.SILK));
		assertThrows(IllegalArgumentException.class,
				() -> ExcavatorEnhancements.EMPTY.withoutSlot(2));
		assertThrows(NullPointerException.class,
				() -> new ExcavatorEnhancements(null, Optional.empty()));
	}

	@Test
	void decodesLegacyBooleanComponents() {
		ExcavatorEnhancements legacySilk = decode("true");
		assertEquals(Optional.of(EnhancementType.SILK), legacySilk.slot(0));
		assertEquals(Optional.empty(), legacySilk.slot(1));
		assertTrue(legacySilk.legacySilkMigrationRequired());
		assertFalse(legacySilk.canonical().legacySilkMigrationRequired());

		assertEquals(ExcavatorEnhancements.EMPTY, decode("false"));
		assertFalse(decode("false").legacySilkMigrationRequired());
	}

	@Test
	void decodesEmptyAndGappedFixedSlotObjects() {
		assertEquals(ExcavatorEnhancements.EMPTY, decode("{}"));

		ExcavatorEnhancements secondSlotOnly = decode("{\"slot_2\":\"filter\"}");
		assertEquals(Optional.empty(), secondSlotOnly.slot(0));
		assertEquals(Optional.of(EnhancementType.FILTER), secondSlotOnly.slot(1));
		assertFalse(secondSlotOnly.legacySilkMigrationRequired());
	}

	@Test
	void roundTripsFixedSlotsAndNeverEncodesLegacyBoolean() {
		ExcavatorEnhancements original = ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.SMELTING);
		JsonElement encoded = ExcavatorEnhancements.CODEC
				.encodeStart(JsonOps.INSTANCE, original)
				.getOrThrow(false, message -> {});

		assertTrue(encoded.isJsonObject());
		assertEquals("silk", encoded.getAsJsonObject().get("slot_1").getAsString());
		assertEquals("smelting", encoded.getAsJsonObject().get("slot_2").getAsString());
		assertEquals(original, ExcavatorEnhancements.CODEC
				.parse(JsonOps.INSTANCE, encoded)
				.getOrThrow(false, message -> {}));

		JsonElement migrated = ExcavatorEnhancements.CODEC
				.encodeStart(JsonOps.INSTANCE, decode("true"))
				.getOrThrow(false, message -> {});
		assertTrue(migrated.isJsonObject());
		assertEquals("silk", migrated.getAsJsonObject().get("slot_1").getAsString());
	}

	@Test
	void reportsCodecErrorsForDuplicateAndIncompatibleSlots() {
		assertTrue(parse("{\"slot_1\":\"silk\",\"slot_2\":\"silk\"}").error().isPresent());
		assertTrue(parse("{\"slot_1\":\"collector\",\"slot_2\":\"void\"}").error().isPresent());
	}

	private static ExcavatorEnhancements decode(String json) {
		return parse(json).getOrThrow(false, message -> {});
	}

	private static com.mojang.serialization.DataResult<ExcavatorEnhancements> parse(String json) {
		return ExcavatorEnhancements.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
	}
}
