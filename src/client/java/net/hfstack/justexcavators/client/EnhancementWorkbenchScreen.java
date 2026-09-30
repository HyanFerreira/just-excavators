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
	private static final int SOURCE_WIDTH = 1213;
	private static final int SOURCE_HEIGHT = 1296;

	public EnhancementWorkbenchScreen(
			EnhancementWorkbenchMenu menu,
			Inventory inventory,
			Component title
	) {
		super(menu, inventory, title, 243, 259);
		this.inventoryLabelX = 14;
		this.inventoryLabelY = 134;
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
		float titleScale = Math.min(1.0F, 162.0F / titleWidth);
		if (titleScale == 1.0F) {
			graphics.centeredText(font, title, imageWidth / 2, 13, 0x404040);
		} else {
			graphics.pose().pushMatrix();
			graphics.pose().translate(imageWidth / 2.0F, 13.0F);
			graphics.pose().scale(titleScale);
			graphics.text(font, title, -titleWidth / 2, 0, 0x404040, false);
			graphics.pose().popMatrix();
		}
		graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
	}
}
