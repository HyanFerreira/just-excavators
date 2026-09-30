package net.hfstack.justexcavators.release;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

final class WorkbenchArtworkTest {
	private static final Path ASSETS = Path.of(
			"src", "main", "resources", "assets", "justexcavators"
	);

	@Test
	void guiUsesVanillaAtlasAndVisibleBounds() throws IOException {
		BufferedImage image = readImage(
				ASSETS.resolve("textures/gui/container/enhancement_workbench.png")
		);

		assertEquals(256, image.getWidth());
		assertEquals(256, image.getHeight());
		assertEquals(0, image.getRGB(176, 0) >>> 24);
		assertEquals(0, image.getRGB(0, 166) >>> 24);
		assertEquals(175, maximumOpaqueCoordinate(image, true));
		assertEquals(165, maximumOpaqueCoordinate(image, false));
	}

	@Test
	void workbenchSlotsUseVanillaSlotPixels() throws IOException {
		BufferedImage image = readImage(
				ASSETS.resolve("textures/gui/container/enhancement_workbench.png")
		);

		assertVanillaSlot(image, 81, 24);
		assertVanillaSlot(image, 52, 53);
		assertVanillaSlot(image, 109, 53);
		assertNoResidualFrame(image, 81, 24);
		assertNoResidualFrame(image, 52, 53);
		assertNoResidualFrame(image, 109, 53);
	}

	@Test
	void screenAndMenuUseVanillaContainerGeometry() throws IOException {
		String screen = Files.readString(Path.of(
				"src", "client", "java", "net", "hfstack", "justexcavators", "client",
				"EnhancementWorkbenchScreen.java"
		));
		String menu = Files.readString(Path.of(
				"src", "main", "java", "net", "hfstack", "justexcavators", "workbench",
				"EnhancementWorkbenchMenu.java"
		));

		assertTrue(screen.contains("private static final int SOURCE_WIDTH = 256;"));
		assertTrue(screen.contains("private static final int SOURCE_HEIGHT = 256;"));
		assertTrue(screen.contains("super(menu, inventory, title, 176, 166);"));
		assertTrue(menu.contains("addSlot(new Slot(tool, 0, 81, 24)"));
		assertTrue(menu.contains("addSlot(coreSlot(0, 52, 53));"));
		assertTrue(menu.contains("addSlot(coreSlot(1, 109, 53));"));
		assertTrue(menu.contains("8 + column * 18, 84 + row * 18"));
		assertTrue(menu.contains("8 + column * 18, 142"));
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
		BufferedImage image = readImage(path);
		assertEquals(width, image.getWidth(), () -> "Unexpected width: " + path);
		assertEquals(height, image.getHeight(), () -> "Unexpected height: " + path);
	}

	private static BufferedImage readImage(Path path) throws IOException {
		BufferedImage image = ImageIO.read(path.toFile());
		assertTrue(image != null, () -> "Missing or invalid PNG: " + path);
		return image;
	}

	private static void assertVanillaSlot(BufferedImage image, int itemX, int itemY) {
		for (int offset = 0; offset < 16; offset++) {
			assertEquals(0x373737, image.getRGB(itemX - 1 + offset, itemY - 1) & 0xFFFFFF);
			assertEquals(0x373737, image.getRGB(itemX - 1, itemY + offset) & 0xFFFFFF);
			assertEquals(0xFFFFFF, image.getRGB(itemX + 16, itemY + offset) & 0xFFFFFF);
			assertEquals(0xFFFFFF, image.getRGB(itemX + offset, itemY + 16) & 0xFFFFFF);
		}
		for (int y = itemY; y < itemY + 16; y++) {
			for (int x = itemX; x < itemX + 16; x++) {
				assertEquals(
						0x8B8B8B,
						image.getRGB(x, y) & 0xFFFFFF,
						"Unexpected line inside slot at " + x + "," + y
				);
			}
		}
	}

	private static void assertNoResidualFrame(BufferedImage image, int itemX, int itemY) {
		Set<Integer> slotEdgeColors = Set.of(0x373737, 0x555555, 0xFFFFFF);
		for (int y = itemY - 4; y <= itemY + 19; y++) {
			for (int x = itemX - 4; x <= itemX + 19; x++) {
				boolean insideSlot = x >= itemX - 1 && x <= itemX + 16
						&& y >= itemY - 1 && y <= itemY + 16;
				if (!insideSlot) {
					assertTrue(
							!slotEdgeColors.contains(image.getRGB(x, y) & 0xFFFFFF),
							"Residual slot frame at " + x + "," + y
					);
				}
			}
		}
	}

	private static int maximumOpaqueCoordinate(BufferedImage image, boolean horizontal) {
		int maximum = -1;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if ((image.getRGB(x, y) >>> 24) != 0) {
					maximum = Math.max(maximum, horizontal ? x : y);
				}
			}
		}
		return maximum;
	}
}
