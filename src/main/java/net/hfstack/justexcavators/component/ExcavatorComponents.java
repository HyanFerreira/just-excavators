package net.hfstack.justexcavators.component;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.excavation.ExcavationMode;

/** Stores excavator state in vanilla NBT on versions before data components. */
public final class ExcavatorComponents {
	private static final String ROOT = "JustExcavators";
	private static final String MODE = "ExcavationMode";
	private static final String SLOT_1 = "EnhancementSlot1";
	private static final String SLOT_2 = "EnhancementSlot2";
	private static final String LEGACY_SILK = "LegacySilkMigrationRequired";

	private ExcavatorComponents() {
	}

	public static ExcavationMode getMode(ItemStack stack) {
		CompoundTag data = data(stack, false);
		return data != null && data.contains(MODE, Tag.TAG_STRING)
				? ExcavationMode.fromSerializedName(data.getString(MODE)).orElse(ExcavationMode.BASIC)
				: ExcavationMode.BASIC;
	}

	public static void setMode(ItemStack stack, ExcavationMode mode) {
		data(stack, true).putString(MODE, mode.getSerializedName());
		stack.getOrCreateTag().putInt("CustomModelData", mode.ordinal());
	}

	public static ExcavatorEnhancements getEnhancements(ItemStack stack) {
		CompoundTag data = data(stack, false);
		if (data == null) return ExcavatorEnhancements.EMPTY;
		Optional<EnhancementType> slot1 = readType(data, SLOT_1);
		Optional<EnhancementType> slot2 = readType(data, SLOT_2);
		try {
			return new ExcavatorEnhancements(slot1, slot2, data.getBoolean(LEGACY_SILK));
		} catch (IllegalArgumentException ignored) {
			return ExcavatorEnhancements.EMPTY;
		}
	}

	public static void setEnhancements(ItemStack stack, ExcavatorEnhancements enhancements) {
		CompoundTag data = data(stack, true);
		writeType(data, SLOT_1, enhancements.slot1());
		writeType(data, SLOT_2, enhancements.slot2());
		data.putBoolean(LEGACY_SILK, enhancements.legacySilkMigrationRequired());
	}

	private static Optional<EnhancementType> readType(CompoundTag data, String key) {
		return data.contains(key, Tag.TAG_STRING)
				? EnhancementType.fromSerializedName(data.getString(key))
				: Optional.empty();
	}

	private static void writeType(CompoundTag data, String key, Optional<EnhancementType> type) {
		if (type.isPresent()) data.putString(key, type.orElseThrow().serializedName());
		else data.remove(key);
	}

	private static CompoundTag data(ItemStack stack, boolean create) {
		CompoundTag root = create ? stack.getOrCreateTag() : stack.getTag();
		if (root == null) return null;
		if (create && !root.contains(ROOT, Tag.TAG_COMPOUND)) root.put(ROOT, new CompoundTag());
		return root.contains(ROOT, Tag.TAG_COMPOUND) ? root.getCompound(ROOT) : null;
	}

	public static void init() {
	}
}
