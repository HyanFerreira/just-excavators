package net.hfstack.justexcavators.release;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class CreativeTabPresentationTest {
	@Test
	void tabIconUsesTheAdvancedDiamondExcavator() throws IOException {
		String source = Files.readString(Path.of(
				"src", "main", "java", "net", "hfstack", "justexcavators", "item",
				"ModCreativeTab.java"
		));

		assertTrue(source.contains(".icon(ModCreativeTab::createIcon)"));
		assertTrue(source.contains("new ItemStack(ModItems.DIAMOND_EXCAVATOR)"));
		assertTrue(source.contains("ExcavatorComponents.setMode(icon, ExcavationMode.ADVANCED);"));
	}
}
