package net.hfstack.justexcavators.enhancement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class EnhancementCoreIndex<T> {
	private final Map<EnhancementType, T> valuesByType;
	private final Map<T, EnhancementType> typesByValue;

	private EnhancementCoreIndex(Map<EnhancementType, T> valuesByType) {
		this.valuesByType = valuesByType;
		IdentityHashMap<T, EnhancementType> typesByValue = new IdentityHashMap<>();
		valuesByType.forEach((type, value) -> {
			EnhancementType duplicate = typesByValue.put(value, type);
			if (duplicate != null) {
				throw new IllegalArgumentException("Value is mapped to both " + duplicate + " and " + type);
			}
		});
		this.typesByValue = Collections.unmodifiableMap(typesByValue);
	}

	static <T> EnhancementCoreIndex<T> create(Map<EnhancementType, T> values) {
		Objects.requireNonNull(values, "values");
		EnumMap<EnhancementType, T> copy = new EnumMap<>(EnhancementType.class);
		values.forEach((type, value) -> copy.put(
				Objects.requireNonNull(type, "type"),
				Objects.requireNonNull(value, type.name())
		));
		if (copy.size() != EnhancementType.values().length) {
			throw new IllegalArgumentException("Every enhancement type must have exactly one value");
		}
		return new EnhancementCoreIndex<>(Collections.unmodifiableMap(copy));
	}

	Optional<EnhancementType> typeOf(T value) {
		return Optional.ofNullable(typesByValue.get(Objects.requireNonNull(value, "value")));
	}

	T valueOf(EnhancementType type) {
		T value = valuesByType.get(Objects.requireNonNull(type, "type"));
		if (value == null) {
			throw new IllegalArgumentException("No value registered for " + type);
		}
		return value;
	}
}
