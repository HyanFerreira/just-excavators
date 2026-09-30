package net.hfstack.justexcavators.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
	void enhancementCoresUseACommonHousingSurroundedByTheirMaterials() throws IOException {
		Map<String, List<String>> materials = Map.of(
				"silk_core", List.of("minecraft:string", "minecraft:emerald"),
				"collector_core", List.of("minecraft:ender_pearl", "minecraft:lapis_lazuli"),
				"smelting_core", List.of("minecraft:magma_cream", "minecraft:blaze_powder"),
				"filter_core", List.of("minecraft:quartz", "minecraft:amethyst_shard"),
				"void_core", List.of("minecraft:ender_eye", "minecraft:ender_pearl")
		);

		for (Map.Entry<String, List<String>> entry : materials.entrySet()) {
			JsonObject recipe = recipe(entry.getKey());
			JsonObject key = recipe.getAsJsonObject("key");
			assertEquals(entry.getValue().get(0), key.get("O").getAsString(), entry.getKey());
			assertEquals(entry.getValue().get(1), key.get("H").getAsString(), entry.getKey());
			assertEquals("justexcavators:core_housing", key.get("M").getAsString(), entry.getKey());
			assertPattern(recipe, "OHO", "HMH", "OHO");
		}
	}

	@Test
	void housingUsesIronIngotsWithNuggetCorners() throws IOException {
		JsonObject recipe = recipe("core_housing");
		JsonObject key = recipe.getAsJsonObject("key");
		assertEquals("minecraft:iron_nugget", key.get("O").getAsString());
		assertEquals("minecraft:iron_ingot", key.get("H").getAsString());
		assertPattern(recipe, "OHO", "H H", "OHO");
		assertEquals("justexcavators:core_housing", recipe.getAsJsonObject("result").get("id").getAsString());
	}

	@Test
	void enhancementWorkbenchUsesPlanksAnvilAndStoneBricks() throws IOException {
		JsonObject recipe = recipe("enhancement_workbench");
		JsonObject key = recipe.getAsJsonObject("key");
		assertEquals("#minecraft:planks", key.get("O").getAsString());
		assertEquals("minecraft:anvil", key.get("H").getAsString());
		assertEquals("minecraft:stone_bricks", key.get("N").getAsString());
		assertPattern(recipe, "OOO", "OHO", "NNN");
		assertEquals(
				"justexcavators:enhancement_workbench",
				recipe.getAsJsonObject("result").get("id").getAsString()
		);
	}

	private static JsonObject model(String name) throws IOException {
		return json(GENERATED_ASSETS.resolve("models/item/" + name + ".json"));
	}

	private static JsonObject recipe(String name) throws IOException {
		Path path = RECIPES.resolve(name + ".json");
		assertTrue(Files.isRegularFile(path), name);
		return json(path);
	}

	private static void assertPattern(JsonObject recipe, String... rows) {
		assertEquals(List.of(rows), recipe.getAsJsonArray("pattern").asList().stream()
				.map(element -> element.getAsString())
				.toList());
	}

	private static JsonObject json(Path path) throws IOException {
		return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
	}
}
