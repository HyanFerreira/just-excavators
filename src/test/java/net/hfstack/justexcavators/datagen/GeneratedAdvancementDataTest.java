package net.hfstack.justexcavators.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.hfstack.justexcavators.enhancement.EnhancementCompatibility;
import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.Test;

final class GeneratedAdvancementDataTest {
	private static final Path ADVANCEMENTS = Path.of(
			"src", "main", "generated", "data", "justexcavators", "advancement"
	);

	@Test
	void enhancementBranchStartsWithWorkbenchAndHousing() throws IOException {
		assertParent("fine_tuning", "justexcavators:bigger_shovel");
		assertParent("power_needs_a_home", "justexcavators:bigger_shovel");

		assertEquals(
				"justexcavators:enhancement_workbench",
				advancement("fine_tuning").getAsJsonObject("display").getAsJsonObject("icon")
						.get("id").getAsString()
		);
		assertEquals(
				"justexcavators:core_housing",
				advancement("power_needs_a_home").getAsJsonObject("display").getAsJsonObject("icon")
						.get("id").getAsString()
		);
	}

	@Test
	void everyEnhancementCoreBranchesFromTheHousing() throws IOException {
		for (String id : List.of(
				"handle_with_care",
				"nothing_left_behind",
				"turn_up_the_heat",
				"only_what_matters",
				"into_the_void"
		)) {
			assertParent(id, "justexcavators:power_needs_a_home");
		}
		assertParent("silken_touch", "justexcavators:handle_with_care");
	}

	@Test
	void fullyLoadedAcceptsEveryOrderedCompatiblePairAndNoInvalidPair() throws IOException {
		JsonObject fullyLoaded = advancement("fully_loaded");
		assertEquals("justexcavators:power_needs_a_home", fullyLoaded.get("parent").getAsString());
		JsonObject criteria = fullyLoaded.getAsJsonObject("criteria");
		assertEquals(14, criteria.size());

		Set<String> expectedCriteria = new HashSet<>();
		for (EnhancementType first : EnhancementType.values()) {
			for (EnhancementType second : EnhancementType.values()) {
				if (EnhancementCompatibility.isValidPair(first, second)) {
					expectedCriteria.add("has_" + first.serializedName() + "_" + second.serializedName());
				}
			}
		}
		assertEquals(expectedCriteria, criteria.keySet());

		for (String criterion : criteria.keySet()) {
			JsonObject enhancements = criteria.getAsJsonObject(criterion)
					.getAsJsonObject("conditions")
					.getAsJsonArray("items").get(0).getAsJsonObject()
					.getAsJsonObject("components")
					.getAsJsonObject("justexcavators:enhancements");
			EnhancementType first = type(enhancements.get("slot_1").getAsString());
			EnhancementType second = type(enhancements.get("slot_2").getAsString());
			assertTrue(EnhancementCompatibility.isValidPair(first, second), criterion);
		}

		JsonArray requirements = fullyLoaded.getAsJsonArray("requirements");
		assertEquals(1, requirements.size());
		assertEquals(14, requirements.get(0).getAsJsonArray().size());
	}

	@Test
	void coreCollectionRequiresAllFiveCoresAtOnce() throws IOException {
		JsonObject collection = advancement("core_collection");
		assertEquals("challenge", collection.getAsJsonObject("display").get("frame").getAsString());
		JsonObject criteria = collection.getAsJsonObject("criteria");
		assertEquals(1, criteria.size());
		assertTrue(criteria.has("has_all_cores"));
		JsonArray items = criteria.getAsJsonObject("has_all_cores")
				.getAsJsonObject("conditions")
				.getAsJsonArray("items");
		assertEquals(5, items.size());
		Set<String> itemIds = new HashSet<>();
		items.forEach(item -> itemIds.add(item.getAsJsonObject().get("items").getAsString()));
		assertEquals(Set.of(
				"justexcavators:silk_core",
				"justexcavators:collector_core",
				"justexcavators:smelting_core",
				"justexcavators:filter_core",
				"justexcavators:void_core"
		), itemIds);
		assertEquals(1, collection.getAsJsonArray("requirements").size());
		assertFalse(itemIds.contains("justexcavators:core_housing"));
	}

	@Test
	void recipeUnlockCriteriaUseMinecraft261Schema() throws IOException {
		Path recipeAdvancements = ADVANCEMENTS.resolve("recipes");
		List<Path> files;
		try (var paths = Files.walk(recipeAdvancements)) {
			files = paths.filter(path -> path.toString().endsWith(".json")).toList();
		}

		assertFalse(files.isEmpty(), "expected generated recipe advancements");
		for (Path file : files) {
			JsonObject advancement = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
			JsonObject conditions = advancement
					.getAsJsonObject("criteria")
					.getAsJsonObject("has_the_recipe")
					.getAsJsonObject("conditions");
			assertTrue(conditions.has("recipe"), file.toString());
			assertFalse(conditions.has("recipes"), file.toString());

			JsonObject rewards = advancement.getAsJsonObject("rewards");
			assertTrue(rewards.has("recipes"), file.toString());
			assertFalse(rewards.has("recipe"), file.toString());
		}
	}

	private static void assertParent(String id, String expectedParent) throws IOException {
		assertEquals(expectedParent, advancement(id).get("parent").getAsString(), id);
	}

	private static JsonObject advancement(String id) throws IOException {
		Path path = ADVANCEMENTS.resolve(id + ".json");
		assertTrue(Files.isRegularFile(path), id);
		return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
	}

	private static EnhancementType type(String name) {
		return EnhancementType.valueOf(name.toUpperCase(Locale.ROOT));
	}
}
