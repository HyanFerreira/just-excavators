package net.hfstack.justexcavators.enhancement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.hfstack.justexcavators.component.ExcavatorEnhancements;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class FilterCorePolicyTest {
	private static Block central;
	private static Block other;
	private static final ActiveEnhancements FILTER = ActiveEnhancements.resolve(
			ExcavatorEnhancements.EMPTY.withSlot(0, EnhancementType.FILTER),
			false,
			false
	);

	@BeforeAll
	static void bootstrapBlocks() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		central = Blocks.SNOW;
		other = Blocks.DIRT;
	}

	@Test
	void centralBlockAlwaysPasses() {
		assertTrue(FilterCorePolicy.shouldProcess(false, FILTER, central, other));
	}

	@Test
	void activeFilterComparesBlockIdentityForAdditionalTargets() {
		BlockState oneLayer = central.defaultBlockState();
		BlockState twoLayers = oneLayer.setValue(SnowLayerBlock.LAYERS, 2);
		assertNotEquals(oneLayer, twoLayers);
		assertTrue(FilterCorePolicy.shouldProcess(
				true, FILTER, oneLayer.getBlock(), twoLayers.getBlock()
		));
		assertFalse(FilterCorePolicy.shouldProcess(true, FILTER, central, other));
	}

	@Test
	void inactiveOrInvalidFilterDoesNotRestrictAdditionalTargets() {
		assertTrue(FilterCorePolicy.shouldProcess(
				true, ActiveEnhancements.EMPTY, central, other
		));
		assertTrue(FilterCorePolicy.shouldProcess(
				true, new ActiveEnhancements(FILTER.enhancements(), false), central, other
		));
	}
}
