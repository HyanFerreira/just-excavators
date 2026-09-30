package net.hfstack.justexcavators.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Test;

final class GeneratedEnhancementCoreDataTest {
	private static final Path ASSETS = Path.of(
			"src", "main", "resources", "assets", "justexcavators", "textures", "item"
	);
	private static final Path GENERATED_ASSETS = Path.of(
			"src", "main", "generated", "assets", "justexcavators"
	);
	private static final Path RECIPES = Path.of(
			"src", "main", "generated", "data", "justexcavators", "recipe"
	);
	private static final List<String> ENHANCEMENT_CORES = List.of(
			"silk_core",
			"collector_core",
			"smelting_core",
			"filter_core",
			"void_core"
	);

	@Test
	void enhancementTexturesUseCanonicalNames() throws IOException {
		for (String name : ENHANCEMENT_CORES) {
			assertTrue(Files.isRegularFile(ASSETS.resolve(name + ".png")), name);
		}
		assertTrue(Files.isRegularFile(ASSETS.resolve("core_housing.png")));
		try (var files = Files.list(ASSETS)) {
			assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("New Piskel")));
		}
	}

	@Test
	void generatedModelsUseEachEnhancementTexture() throws IOException {
		for (String name : ENHANCEMENT_CORES) {
			assertEquals(
					"justexcavators:item/" + name,
					model(name).getAsJsonObject("textures").get("layer0").getAsString()
			);
		}
		assertEquals(
				"justexcavators:item/core_housing",
				model("core_housing").getAsJsonObject("textures").get("layer0").getAsString()
		);
		assertTrue(Files.isRegularFile(GENERATED_ASSETS.resolve("items/core_housing.json")));
	}

	@Test
	void everyEnhancementCoreRequiresACommonHousing() throws IOException {
		for (String name : ENHANCEMENT_CORES) {
			Path path = RECIPES.resolve(name + ".json");
			assertTrue(Files.isRegularFile(path), name);
			JsonObject recipe = json(path);
			JsonObject key = recipe.getAsJsonObject("key");
			assertTrue(key.has("H"), name);
			assertEquals(
					"justexcavators:core_housing",
					key.get("H").getAsString(),
					name
			);
		}
	}

	@Test
	void housingUsesAnIronNuggetRingRecipe() throws IOException {
		Path path = RECIPES.resolve("core_housing.json");
		assertTrue(Files.isRegularFile(path));
		JsonObject recipe = json(path);
		assertEquals("minecraft:iron_nugget", recipe.getAsJsonObject("key").get("N").getAsString());
		assertEquals(List.of("NNN", "N N", "NNN"), recipe.getAsJsonArray("pattern").asList().stream()
				.map(element -> element.getAsString())
				.toList());
		assertEquals("justexcavators:core_housing", recipe.getAsJsonObject("result").get("id").getAsString());
	}

	private static JsonObject model(String name) throws IOException {
		return json(GENERATED_ASSETS.resolve("models/item/" + name + ".json"));
	}

	private static JsonObject json(Path path) throws IOException {
		return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
	}
}
