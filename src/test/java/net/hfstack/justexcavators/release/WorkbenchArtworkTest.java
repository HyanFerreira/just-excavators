package net.hfstack.justexcavators.release;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

final class WorkbenchArtworkTest {
	private static final Path ASSETS = Path.of(
			"src", "main", "resources", "assets", "justexcavators"
	);

	@Test
	void guiUsesItsNativeRuntimeDimensions() throws IOException {
		assertDimensions(
				ASSETS.resolve("textures/gui/container/enhancement_workbench.png"),
				243,
				259
		);
	}

	@Test
	void blockTexturesUseNativeMinecraftResolution() throws IOException {
		for (String face : new String[]{"top", "front", "back", "side", "bottom"}) {
			assertDimensions(
					ASSETS.resolve("textures/block/enhancement_workbench_" + face + ".png"),
					16,
					16
			);
		}
	}

	@Test
	void blockstateAndItemUseTheCustomWorkbenchModel() throws IOException {
		String blockstate = Files.readString(ASSETS.resolve("blockstates/enhancement_workbench.json"));
		String item = Files.readString(Path.of(
				"src", "main", "generated", "assets", "justexcavators", "items",
				"enhancement_workbench.json"
		));

		assertTrue(blockstate.contains("justexcavators:block/enhancement_workbench"));
		assertTrue(item.contains("justexcavators:block/enhancement_workbench"));
	}

	private static void assertDimensions(Path path, int width, int height) throws IOException {
		BufferedImage image = ImageIO.read(path.toFile());
		assertTrue(image != null, () -> "Missing or invalid PNG: " + path);
		assertEquals(width, image.getWidth(), () -> "Unexpected width: " + path);
		assertEquals(height, image.getHeight(), () -> "Unexpected height: " + path);
	}
}
