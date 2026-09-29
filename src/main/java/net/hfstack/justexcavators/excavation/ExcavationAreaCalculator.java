package net.hfstack.justexcavators.excavation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class ExcavationAreaCalculator {
	private static final Comparator<PlaneOffset> CENTER_OUT = Comparator
			.comparingInt(PlaneOffset::distanceSquared)
			.thenComparingInt(PlaneOffset::first)
			.thenComparingInt(PlaneOffset::second);

	private ExcavationAreaCalculator() {
	}

	public static List<BlockPos> calculate(BlockPos origin, Direction hitFace, ExcavationMode mode) {
		Objects.requireNonNull(origin, "origin");
		Objects.requireNonNull(hitFace, "hitFace");
		Objects.requireNonNull(mode, "mode");

		List<PlaneOffset> offsets = centeredOffsets(mode.width(), mode.height());
		List<BlockPos> positions = new ArrayList<>(mode.maxBlocks());
		Direction inward = hitFace.getOpposite();

		for (int depth = 0; depth < mode.depth(); depth++) {
			BlockPos layerOrigin = origin.relative(inward, depth);
			for (PlaneOffset offset : offsets) {
				positions.add(applyPlaneOffset(layerOrigin, hitFace.getAxis(), offset));
			}
		}

		return List.copyOf(positions);
	}

	private static List<PlaneOffset> centeredOffsets(int width, int height) {
		List<PlaneOffset> offsets = new ArrayList<>(width * height);
		int halfWidth = width / 2;
		int halfHeight = height / 2;

		for (int first = -halfWidth; first <= halfWidth; first++) {
			for (int second = -halfHeight; second <= halfHeight; second++) {
				offsets.add(new PlaneOffset(first, second));
			}
		}

		offsets.sort(CENTER_OUT);
		return offsets;
	}

	private static BlockPos applyPlaneOffset(
			BlockPos origin,
			Direction.Axis normalAxis,
			PlaneOffset offset
	) {
		return switch (normalAxis) {
			case X -> origin.offset(0, offset.second(), offset.first());
			case Y -> origin.offset(offset.first(), 0, offset.second());
			case Z -> origin.offset(offset.first(), offset.second(), 0);
		};
	}

	private record PlaneOffset(int first, int second) {
		private int distanceSquared() {
			return first * first + second * second;
		}
	}
}
