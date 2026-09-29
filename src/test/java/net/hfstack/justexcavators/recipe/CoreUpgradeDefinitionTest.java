package net.hfstack.justexcavators.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import net.hfstack.justexcavators.excavation.ExcavationMode;

final class CoreUpgradeDefinitionTest {
	@Test
	void recipeCodecReadsTheTargetMode() {
		CoreUpgradeDefinition definition = CoreUpgradeDefinition.CODEC
				.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"mode\":\"wide\"}"))
				.getOrThrow();

		assertEquals(ExcavationMode.WIDE, definition.mode());
	}

	@Test
	void sameProfileIsRejected() {
		assertFalse(CoreUpgradeDefinition.canApply(ExcavationMode.BASIC, ExcavationMode.BASIC));
		assertTrue(CoreUpgradeDefinition.canApply(ExcavationMode.BASIC, ExcavationMode.DEEP));
		assertTrue(CoreUpgradeDefinition.canApply(ExcavationMode.DEEP, ExcavationMode.WIDE));
	}
}
