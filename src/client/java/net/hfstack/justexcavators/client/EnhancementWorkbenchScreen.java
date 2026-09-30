package net.hfstack.justexcavators.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.workbench.EnhancementWorkbenchMenu;

public final class EnhancementWorkbenchScreen
		extends AbstractContainerScreen<EnhancementWorkbenchMenu> {
	private static final Identifier TEXTURE = JustExcavators.id(
			"textures/gui/container/enhancement_workbench.png"
	);
	private static final int SOURCE_WIDTH = 256;
	private static final int SOURCE_HEIGHT = 256;
	private static final float TITLE_MAX_WIDTH = 118.0F;
	private static final int TITLE_Y = 7;
	private static final int TITLE_COLOR = 0xFF404040;

	public EnhancementWorkbenchScreen(
			EnhancementWorkbenchMenu menu,
			Inventory inventory,
			Component title
	) {
		super(menu, inventory, title, 176, 166);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				TEXTURE,
				leftPos,
				topPos,
				0.0F,
				0.0F,
				imageWidth,
				imageHeight,
				SOURCE_WIDTH,
				SOURCE_HEIGHT
		);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int titleWidth = font.width(title);
		float titleScale = Math.min(1.0F, TITLE_MAX_WIDTH / titleWidth);
		if (titleScale == 1.0F) {
			graphics.centeredText(font, title, imageWidth / 2, TITLE_Y, TITLE_COLOR);
		} else {
			graphics.pose().pushMatrix();
			graphics.pose().translate(imageWidth / 2.0F, TITLE_Y);
			graphics.pose().scale(titleScale);
			graphics.text(font, title, -titleWidth / 2, 0, TITLE_COLOR, false);
			graphics.pose().popMatrix();
		}
	}
}
