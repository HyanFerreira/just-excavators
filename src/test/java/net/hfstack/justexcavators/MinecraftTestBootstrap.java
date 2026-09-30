package net.hfstack.justexcavators;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;

public final class MinecraftTestBootstrap {
	private static HolderLookup.Provider registries;

	private MinecraftTestBootstrap() {
	}

	public static synchronized HolderLookup.Provider registries() {
		if (registries == null) {
			SharedConstants.tryDetectVersion();
			Bootstrap.bootStrap();
			registries = VanillaRegistries.createWorldLookup();
			BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
					.forEach(pending -> pending.apply());
		}
		return registries;
	}
}
