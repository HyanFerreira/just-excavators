package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;

import org.junit.jupiter.api.Test;

final class EnhancementCompatibilityTest {
	@Test
	void acceptsEveryApprovedPairInEitherOrder() {
		List<List<EnhancementType>> approvedPairs = List.of(
				List.of(EnhancementType.SILK, EnhancementType.COLLECTOR),
				List.of(EnhancementType.SILK, EnhancementType.SMELTING),
				List.of(EnhancementType.SILK, EnhancementType.FILTER),
				List.of(EnhancementType.COLLECTOR, EnhancementType.SMELTING),
				List.of(EnhancementType.COLLECTOR, EnhancementType.FILTER),
				List.of(EnhancementType.SMELTING, EnhancementType.FILTER),
				List.of(EnhancementType.FILTER, EnhancementType.VOID)
		);

		for (List<EnhancementType> pair : approvedPairs) {
			assertTrue(EnhancementCompatibility.isValidPair(pair.get(0), pair.get(1)), pair.toString());
			assertTrue(EnhancementCompatibility.isValidPair(pair.get(1), pair.get(0)), pair.toString());
		}
	}

	@Test
	void rejectsEveryForbiddenPairInEitherOrder() {
		List<List<EnhancementType>> forbiddenPairs = List.of(
				List.of(EnhancementType.SILK, EnhancementType.VOID),
				List.of(EnhancementType.COLLECTOR, EnhancementType.VOID),
				List.of(EnhancementType.SMELTING, EnhancementType.VOID)
		);

		for (List<EnhancementType> pair : forbiddenPairs) {
			assertFalse(EnhancementCompatibility.isValidPair(pair.get(0), pair.get(1)), pair.toString());
			assertFalse(EnhancementCompatibility.isValidPair(pair.get(1), pair.get(0)), pair.toString());
		}
	}

	@Test
	void rejectsDuplicateCores() {
		for (EnhancementType type : EnhancementType.values()) {
			assertFalse(EnhancementCompatibility.isValidPair(type, type), type.name());
		}
	}

	@Test
	void acceptsEmptyAndSingleCoreSlots() {
		assertTrue(EnhancementCompatibility.isValidSlots(Optional.empty(), Optional.empty()));
		for (EnhancementType type : EnhancementType.values()) {
			assertTrue(EnhancementCompatibility.isValidSlots(Optional.of(type), Optional.empty()), type.name());
			assertTrue(EnhancementCompatibility.isValidSlots(Optional.empty(), Optional.of(type)), type.name());
		}
	}

	@Test
	void appliesCoreEnchantmentCompatibility() {
		assertFalse(compatibleWithEnchantments(EnhancementType.SILK, true, false));
		assertFalse(compatibleWithEnchantments(EnhancementType.SILK, false, true));
		assertTrue(compatibleWithEnchantments(EnhancementType.SMELTING, true, false));
		assertTrue(compatibleWithEnchantments(EnhancementType.SMELTING, false, true));
		assertFalse(compatibleWithEnchantments(EnhancementType.VOID, true, false));
		assertFalse(compatibleWithEnchantments(EnhancementType.VOID, false, true));
		assertTrue(compatibleWithEnchantments(EnhancementType.FILTER, false, false));
	}

	@Test
	void serializesEveryEnhancementTypeByItsLowercaseIdentifier() {
		for (EnhancementType type : EnhancementType.values()) {
			String identifier = type.name().toLowerCase(java.util.Locale.ROOT);
			EnhancementType decoded = EnhancementType.CODEC
					.parse(JsonOps.INSTANCE, new JsonPrimitive(identifier))
					.getOrThrow();
			assertEquals(type, decoded);
			assertEquals(new JsonPrimitive(identifier), EnhancementType.CODEC
					.encodeStart(JsonOps.INSTANCE, type)
					.getOrThrow());
		}
	}

	@Test
	void rejectsUnknownEnhancementTypeIdentifier() {
		assertTrue(EnhancementType.CODEC
				.parse(JsonOps.INSTANCE, new JsonPrimitive("unknown"))
				.error()
				.isPresent());
	}

	private static boolean compatibleWithEnchantments(
			EnhancementType type,
			boolean hasSilkTouch,
			boolean hasFortune
	) {
		return EnhancementCompatibility.areEnchantmentsCompatible(
				Optional.of(type),
				Optional.empty(),
				hasSilkTouch,
				hasFortune
		);
	}
}
