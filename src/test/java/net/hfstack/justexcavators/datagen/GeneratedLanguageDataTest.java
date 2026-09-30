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

	private static String translation(String language, String key) throws IOException {
		JsonObject translations = JsonParser.parseString(Files.readString(
				LANG_DIRECTORY.resolve(language + ".json")
		)).getAsJsonObject();
		return translations.has(key) ? translations.get(key).getAsString() : null;
	}
}
