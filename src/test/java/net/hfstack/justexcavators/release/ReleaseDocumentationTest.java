package net.hfstack.justexcavators.release;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class ReleaseDocumentationTest {
	@Test
	void readmeDescribesTheCurrentEnhancementWorkflow() throws IOException {
		String readme = Files.readString(Path.of("README.md"));

		assertTrue(readme.contains("Bancada de Aprimoramento"));
		assertTrue(readme.contains("Núcleo Coletor"));
		assertTrue(readme.contains("Núcleo de Fundição"));
		assertTrue(readme.contains("Núcleo de Filtro"));
		assertTrue(readme.contains("Núcleo do Vazio"));
		assertFalse(readme.contains("Silk enhancement: combine any Excavator with a Silk Core in the Smithing"));
		assertFalse(readme.contains("A Grindstone removes ordinary enchantments but preserves Silk Touch"));
	}

	@Test
	void changelogRecordsTheCurrentVersionAndEnhancementCores() throws IOException {
		String changelog = Files.readString(Path.of("CHANGELOG.md"));

		assertTrue(changelog.contains("## [1.0.0]"));
		assertTrue(changelog.contains("Enhancement Workbench"));
		assertTrue(changelog.contains("Silk, Collector, Smelting, Filter, and Void"));
	}
}
