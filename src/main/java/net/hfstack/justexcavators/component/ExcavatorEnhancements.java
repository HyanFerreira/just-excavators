package net.hfstack.justexcavators.component;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.hfstack.justexcavators.enhancement.EnhancementCompatibility;
import net.hfstack.justexcavators.enhancement.EnhancementType;

public record ExcavatorEnhancements(
		Optional<EnhancementType> slot1,
		Optional<EnhancementType> slot2
) {
	public static final ExcavatorEnhancements EMPTY = new ExcavatorEnhancements(
			Optional.empty(),
			Optional.empty()
	);

	private static final Codec<RawSlots> FIXED_SLOTS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EnhancementType.CODEC.optionalFieldOf("slot_1").forGetter(RawSlots::slot1),
			EnhancementType.CODEC.optionalFieldOf("slot_2").forGetter(RawSlots::slot2)
	).apply(instance, RawSlots::new));

	private static final Codec<ExcavatorEnhancements> NEW_CODEC = FIXED_SLOTS_CODEC.flatXmap(
			RawSlots::validate,
			enhancements -> DataResult.success(new RawSlots(enhancements.slot1, enhancements.slot2))
	);

	public static final Codec<ExcavatorEnhancements> CODEC = Codec.either(Codec.BOOL, NEW_CODEC).xmap(
			either -> either.map(
					hasSilk -> hasSilk
							? new ExcavatorEnhancements(Optional.of(EnhancementType.SILK), Optional.empty())
							: EMPTY,
					Function.identity()
			),
			enhancements -> Either.right(enhancements)
	);

	public ExcavatorEnhancements {
		slot1 = Objects.requireNonNull(slot1, "slot1");
		slot2 = Objects.requireNonNull(slot2, "slot2");
		if (!EnhancementCompatibility.isValidSlots(slot1, slot2)) {
			throw new IllegalArgumentException("Invalid enhancement slot combination: " + slot1 + ", " + slot2);
		}
	}

	public Optional<EnhancementType> slot(int index) {
		return switch (index) {
			case 0 -> slot1;
			case 1 -> slot2;
			default -> throw invalidSlot(index);
		};
	}

	public boolean has(EnhancementType type) {
		Objects.requireNonNull(type, "type");
		return slot1.filter(type::equals).isPresent() || slot2.filter(type::equals).isPresent();
	}

	public int size() {
		return (slot1.isPresent() ? 1 : 0) + (slot2.isPresent() ? 1 : 0);
	}

	public boolean canSet(int index, EnhancementType type) {
		Objects.requireNonNull(type, "type");
		return switch (index) {
			case 0 -> EnhancementCompatibility.isValidSlots(Optional.of(type), slot2);
			case 1 -> EnhancementCompatibility.isValidSlots(slot1, Optional.of(type));
			default -> throw invalidSlot(index);
		};
	}

	public ExcavatorEnhancements withSlot(int index, EnhancementType type) {
		if (!canSet(index, type)) {
			throw new IllegalArgumentException("Enhancement " + type + " is not valid in slot " + index);
		}
		return index == 0
				? new ExcavatorEnhancements(Optional.of(type), slot2)
				: new ExcavatorEnhancements(slot1, Optional.of(type));
	}

	public ExcavatorEnhancements withoutSlot(int index) {
		return switch (index) {
			case 0 -> new ExcavatorEnhancements(Optional.empty(), slot2);
			case 1 -> new ExcavatorEnhancements(slot1, Optional.empty());
			default -> throw invalidSlot(index);
		};
	}

	private static IllegalArgumentException invalidSlot(int index) {
		return new IllegalArgumentException("Enhancement slot index must be 0 or 1: " + index);
	}

	private record RawSlots(Optional<EnhancementType> slot1, Optional<EnhancementType> slot2) {
		private DataResult<ExcavatorEnhancements> validate() {
			if (!EnhancementCompatibility.isValidSlots(slot1, slot2)) {
				return DataResult.error(() -> "Invalid enhancement slot combination: " + slot1 + ", " + slot2);
			}
			return DataResult.success(new ExcavatorEnhancements(slot1, slot2));
		}
	}
}
