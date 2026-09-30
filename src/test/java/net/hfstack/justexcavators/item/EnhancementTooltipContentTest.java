package net.hfstack.justexcavators.item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementType;

import org.junit.jupiter.api.Test;

final class EnhancementTooltipContentTest {
	@Test
	void enhancementTypesExposeStableLocalizedKeysAndOnlyVoidWarns() {
		for (EnhancementType type : EnhancementType.values()) {
			String base = "enhancement.justexcavators." + type.serializedName();
			assertEquals(base, type.translationKey());
			assertEquals(base + ".description", type.descriptionTranslationKey());
			assertEquals(type == EnhancementType.VOID, type.warningTranslationKey().isPresent());
		}
		assertEquals(
				"enhancement.justexcavators.void.warning",
				EnhancementType.VOID.warningTranslationKey().orElseThrow()
		);
	}

	@Test
	void everyCoreItemShowsItsDescriptionAndOnlyVoidShowsAWarning() {
		for (EnhancementType type : EnhancementType.values()) {
			List<Component> lines = EnhancementCoreItem.tooltipLines(type);
			assertEquals(type.descriptionTranslationKey(), translatable(lines.getFirst()).getKey());
			assertEquals(type == EnhancementType.VOID ? 2 : 1, lines.size());
		}
		assertEquals(
				EnhancementType.VOID.warningTranslationKey().orElseThrow(),
				translatable(EnhancementCoreItem.tooltipLines(EnhancementType.VOID).get(1)).getKey()
		);
	}

	@Test
	void headerReportsZeroThroughTwoInstalledCores() {
		assertHeader(ExcavatorEnhancements.EMPTY, 0);
		assertHeader(ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.SILK), 1);
		assertHeader(ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.COLLECTOR), 2);
	}

	@Test
	void entriesFollowFixedSlotOrder() {
		List<Component> lines = EnhancementTooltipContent.lines(ExcavatorEnhancements.EMPTY
				.withSlot(0, EnhancementType.SILK)
				.withSlot(1, EnhancementType.FILTER));

		assertSlot(lines.get(1), 1, EnhancementType.SILK);
		assertSlot(lines.get(2), 2, EnhancementType.FILTER);
	}

	@Test
	void secondSlotOnlyKeepsItsVisibleSlotIdentity() {
		List<Component> lines = EnhancementTooltipContent.lines(
				ExcavatorEnhancements.EMPTY.withSlot(1, EnhancementType.VOID)
		);

		assertEquals(2, lines.size());
		assertSlot(lines.get(1), 2, EnhancementType.VOID);
	}

	private static void assertHeader(ExcavatorEnhancements enhancements, int count) {
		TranslatableContents header = translatable(EnhancementTooltipContent.lines(enhancements).getFirst());
		assertEquals("tooltip.justexcavators.enhancements", header.getKey());
		assertArrayEquals(new Object[] { count, 2 }, header.getArgs());
	}

	private static void assertSlot(Component line, int slot, EnhancementType type) {
		TranslatableContents contents = translatable(line);
		assertEquals("tooltip.justexcavators.enhancement_slot", contents.getKey());
		assertEquals(slot, contents.getArgs()[0]);
		assertTrue(contents.getArgs()[1] instanceof Component);
		assertEquals(type.translationKey(), translatable((Component) contents.getArgs()[1]).getKey());
	}

	private static TranslatableContents translatable(Component component) {
		assertTrue(component.getContents() instanceof TranslatableContents);
		return (TranslatableContents) component.getContents();
	}
}
