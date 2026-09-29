package net.hfstack.justexcavators.component;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.excavation.ExcavationMode;

public final class ExcavatorComponents {
	public static final DataComponentType<ExcavationMode> EXCAVATION_MODE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			JustExcavators.id("excavation_mode"),
			DataComponentType.<ExcavationMode>builder()
					.persistent(ExcavationMode.CODEC)
					.networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(ExcavationMode.CODEC))
					.build()
	);

	public static final DataComponentType<ExcavatorEnhancements> ENHANCEMENTS = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			JustExcavators.id("enhancements"),
			DataComponentType.<ExcavatorEnhancements>builder()
					.persistent(ExcavatorEnhancements.CODEC)
					.networkSynchronized(ByteBufCodecs.fromCodecWithRegistries(ExcavatorEnhancements.CODEC))
					.build()
	);

	private ExcavatorComponents() {
	}

	public static void init() {
		// Loading this class performs the component registrations.
	}
}
