package net.hfstack.justexcavators.client;

import java.util.List;

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
	private static final Identifier INFO_TEXTURE = JustExcavators.id(
			"textures/gui/container/info.png"
	);
	private static final Identifier INFO_HIGHLIGHTED_TEXTURE = JustExcavators.id(
			"textures/gui/container/info_highlighted.png"
	);
	private static final int SOURCE_WIDTH = 256;
	private static final int SOURCE_HEIGHT = 256;
	private static final int INFO_SIZE = 14;
	private static final int INFO_X = 154;
	private static final int INFO_Y = 8;
	private static final int TITLE_X = 8;
	private static final int TITLE_Y = 6;
	private static final float TITLE_SCALE = 0.9F;
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

		boolean infoHovered = mouseX >= leftPos + INFO_X
				&& mouseX < leftPos + INFO_X + INFO_SIZE
				&& mouseY >= topPos + INFO_Y
				&& mouseY < topPos + INFO_Y + INFO_SIZE;
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				infoHovered ? INFO_HIGHLIGHTED_TEXTURE : INFO_TEXTURE,
				leftPos + INFO_X,
				topPos + INFO_Y,
				0.0F,
				0.0F,
				INFO_SIZE,
				INFO_SIZE,
				INFO_SIZE,
				INFO_SIZE
		);
		if (infoHovered) {
			graphics.setComponentTooltipForNextFrame(
					font,
					List.of(
							Component.translatable("gui.justexcavators.enhancement_workbench.info"),
							Component.translatable("gui.justexcavators.enhancement_workbench.info.tool_slot"),
							Component.translatable("gui.justexcavators.enhancement_workbench.info.core_slots")
					),
					mouseX,
					mouseY
			);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.pose().pushMatrix();
		graphics.pose().translate(TITLE_X, TITLE_Y);
		graphics.pose().scale(TITLE_SCALE);
		graphics.text(font, title, 0, 0, TITLE_COLOR, false);
		graphics.pose().popMatrix();
	}
}
