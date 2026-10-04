package net.hfstack.justexcavators.excavation;

import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.Optional;

import net.minecraft.util.StringRepresentable;

public enum ExcavationMode implements StringRepresentable {
	BASIC("basic", 3, 3, 1),
	DEEP("deep", 3, 3, 3),
	WIDE("wide", 5, 5, 1),
	ADVANCED("advanced", 5, 5, 3);

	public static final Codec<ExcavationMode> CODEC = StringRepresentable.fromEnum(ExcavationMode::values);

	private final String serializedName;
	private final int width;
	private final int height;
	private final int depth;

	ExcavationMode(String serializedName, int width, int height, int depth) {
		this.serializedName = serializedName;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	@Override
	public String getSerializedName() {
		return serializedName;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int depth() {
		return depth;
	}

	public int maxBlocks() {
		return width * height * depth;
	}

	public String translationKey() {
		return "excavation_mode.justexcavators." + serializedName;
	}

	public static Optional<ExcavationMode> fromSerializedName(String name) {
		return Arrays.stream(values()).filter(mode -> mode.serializedName.equals(name)).findFirst();
	}
}
