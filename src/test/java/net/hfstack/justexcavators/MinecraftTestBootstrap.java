package net.hfstack.justexcavators;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
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
			registries = VanillaRegistries.createLookup();
		}
		return registries;
	}
}
