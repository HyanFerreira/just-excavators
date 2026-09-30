package net.hfstack.justexcavators.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.Test;

final class GeneratedLanguageDataTest {
	private static final Path LANG_DIRECTORY = Path.of(
			"src", "main", "generated", "assets", "justexcavators", "lang"
	);

	@Test
	void translatesExcavatorItemTagInEverySupportedLanguage() throws IOException {
		assertEquals("Excavators", translation("en_us", "tag.item.justexcavators.excavators"));
		assertEquals("Escavadoras", translation("pt_br", "tag.item.justexcavators.excavators"));
	}

	@Test
	void usesConcisePortugueseWorkbenchNameForBlockAndContainer() throws IOException {
		assertEquals(
				"Bancada de Aprimoramento",
				translation("pt_br", "block.justexcavators.enhancement_workbench")
		);
		assertEquals(
				"Bancada de Aprimoramento",
				translation("pt_br", "container.justexcavators.enhancement_workbench")
		);
	}

	@Test
	void translatesCoreHousingInEverySupportedLanguage() throws IOException {
		assertEquals("Core Housing", translation("en_us", "item.justexcavators.core_housing"));
		assertEquals("Estrutura de Núcleo", translation("pt_br", "item.justexcavators.core_housing"));
	}

	@Test
	void translatesEveryEnhancementAdvancementInEverySupportedLanguage() throws IOException {
		for (String id : new String[] {
				"fine_tuning",
				"power_needs_a_home",
				"nothing_left_behind",
				"turn_up_the_heat",
				"only_what_matters",
				"into_the_void",
				"fully_loaded",
				"core_collection"
		}) {
			for (String language : new String[] {"en_us", "pt_br"}) {
				assertTranslationExists(language, "advancement.justexcavators." + id + ".title");
				assertTranslationExists(language, "advancement.justexcavators." + id + ".description");
			}
		}
	}

	private static String translation(String language, String key) throws IOException {
		JsonObject translations = JsonParser.parseString(Files.readString(
				LANG_DIRECTORY.resolve(language + ".json")
		)).getAsJsonObject();
		return translations.has(key) ? translations.get(key).getAsString() : null;
	}

	private static void assertTranslationExists(String language, String key) throws IOException {
		String value = translation(language, key);
		org.junit.jupiter.api.Assertions.assertNotNull(value, language + ":" + key);
		org.junit.jupiter.api.Assertions.assertFalse(value.isBlank(), language + ":" + key);
	}
}
