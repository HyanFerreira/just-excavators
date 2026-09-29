package net.hfstack.justexcavators.component;

import com.mojang.serialization.Codec;

public record ExcavatorEnhancements(boolean silk) {
	public static final ExcavatorEnhancements NONE = new ExcavatorEnhancements(false);
	public static final Codec<ExcavatorEnhancements> CODEC = Codec.BOOL.xmap(
			ExcavatorEnhancements::new,
			ExcavatorEnhancements::silk
	);

	public ExcavatorEnhancements withSilk() {
		return silk ? this : new ExcavatorEnhancements(true);
	}
}
