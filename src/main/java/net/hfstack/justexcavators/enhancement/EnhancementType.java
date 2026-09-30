package net.hfstack.justexcavators.enhancement;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum EnhancementType {
	SILK,
	COLLECTOR,
	SMELTING,
	FILTER,
	VOID;

	private static final Map<String, EnhancementType> BY_SERIALIZED_NAME = java.util.Arrays.stream(values())
			.collect(Collectors.toUnmodifiableMap(EnhancementType::serializedName, Function.identity()));

	public static final Codec<EnhancementType> CODEC = Codec.STRING.comapFlatMap(
			name -> {
				EnhancementType type = BY_SERIALIZED_NAME.get(name);
				return type == null
						? DataResult.error(() -> "Unknown enhancement type: " + name)
						: DataResult.success(type);
			},
			EnhancementType::serializedName
	);

	public String serializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public String translationKey() {
		return "enhancement.justexcavators." + serializedName();
	}

	public String descriptionTranslationKey() {
		return translationKey() + ".description";
	}

	public Optional<String> warningTranslationKey() {
		return this == VOID
				? Optional.of(translationKey() + ".warning")
				: Optional.empty();
	}
}
