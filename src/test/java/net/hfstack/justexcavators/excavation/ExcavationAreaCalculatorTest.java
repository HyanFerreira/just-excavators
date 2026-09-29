package net.hfstack.justexcavators.excavation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

final class ExcavationAreaCalculatorTest {
	private static final BlockPos ORIGIN = new BlockPos(10, 20, 30);

	@Test
	void everyModeHasTheExpectedSizeWithoutDuplicatesOnEveryFace() {
		for (ExcavationMode mode : ExcavationMode.values()) {
			for (Direction face : Direction.values()) {
				List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, face, mode);

				assertEquals(mode.maxBlocks(), positions.size(), mode + " on " + face);
				assertEquals(positions.size(), new HashSet<>(positions).size(), mode + " on " + face);
				assertEquals(ORIGIN, positions.getFirst(), mode + " on " + face);
			}
		}
	}

	@Test
	void topFaceProducesAHorizontalCenteredPlane() {
		List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, Direction.UP, ExcavationMode.BASIC);

		assertTrue(positions.stream().allMatch(pos -> pos.getY() == ORIGIN.getY()));
		assertEquals(ORIGIN.getX() - 1, positions.stream().mapToInt(BlockPos::getX).min().orElseThrow());
		assertEquals(ORIGIN.getX() + 1, positions.stream().mapToInt(BlockPos::getX).max().orElseThrow());
		assertEquals(ORIGIN.getZ() - 1, positions.stream().mapToInt(BlockPos::getZ).min().orElseThrow());
		assertEquals(ORIGIN.getZ() + 1, positions.stream().mapToInt(BlockPos::getZ).max().orElseThrow());
	}

	@Test
	void northFaceProducesAVerticalCenteredPlane() {
		List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, Direction.NORTH, ExcavationMode.WIDE);

		assertTrue(positions.stream().allMatch(pos -> pos.getZ() == ORIGIN.getZ()));
		assertEquals(ORIGIN.getX() - 2, positions.stream().mapToInt(BlockPos::getX).min().orElseThrow());
		assertEquals(ORIGIN.getX() + 2, positions.stream().mapToInt(BlockPos::getX).max().orElseThrow());
		assertEquals(ORIGIN.getY() - 2, positions.stream().mapToInt(BlockPos::getY).min().orElseThrow());
		assertEquals(ORIGIN.getY() + 2, positions.stream().mapToInt(BlockPos::getY).max().orElseThrow());
	}

	@Test
	void everyBasicPlaneIsPerpendicularToItsHitFace() {
		for (Direction face : Direction.values()) {
			List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, face, ExcavationMode.BASIC);

			assertTrue(positions.stream().allMatch(pos -> switch (face.getAxis()) {
				case X -> pos.getX() == ORIGIN.getX();
				case Y -> pos.getY() == ORIGIN.getY();
				case Z -> pos.getZ() == ORIGIN.getZ();
			}), face.toString());
		}
	}

	@Test
	void deepModeAdvancesIntoEveryHitFace() {
		for (Direction face : Direction.values()) {
			List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, face, ExcavationMode.DEEP);

			assertTrue(positions.contains(ORIGIN.relative(face.getOpposite(), 2)), face.toString());
			assertFalse(positions.contains(ORIGIN.relative(face)), face.toString());
		}
	}

	@Test
	void deepModeOrdersLayersBeforeMovingDeeper() {
		List<BlockPos> positions = ExcavationAreaCalculator.calculate(ORIGIN, Direction.SOUTH, ExcavationMode.DEEP);

		for (int index = 0; index < positions.size(); index++) {
			assertEquals(index / 9, ORIGIN.getZ() - positions.get(index).getZ());
		}
	}

	@Test
	void advancedModeProducesThreeOrderedFiveByFiveLayers() {
		List<BlockPos> positions = ExcavationAreaCalculator.calculate(
				ORIGIN,
				Direction.NORTH,
				ExcavationMode.ADVANCED
		);

		assertEquals(75, positions.size());
		for (int index = 0; index < positions.size(); index++) {
			assertEquals(index / 25, positions.get(index).getZ() - ORIGIN.getZ());
		}
	}
}
