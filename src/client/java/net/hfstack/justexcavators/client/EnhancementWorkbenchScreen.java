package net.hfstack.justexcavators.client;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.hfstack.justexcavators.JustExcavators;
import net.hfstack.justexcavators.enhancement.EnhancementCompatibility;
import net.hfstack.justexcavators.enhancement.EnhancementCoreCatalog;
import net.hfstack.justexcavators.enhancement.EnhancementType;
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
	private static final int INFO_X = 180;
	private static final int INFO_Y = 4;
	private static final int TITLE_CENTER_X = 88;
	private static final int TITLE_Y = 7;
	private static final float TITLE_SCALE = 0.9F;
	private static final int TITLE_MAX_WIDTH = 116;
	private static final int TITLE_COLOR = 0xFFFFFFFF;
	private static final int PANEL_X = 7;
	private static final int PANEL_Y = 22;
	private static final int PANEL_WIDTH = 162;
	private static final int PANEL_HEIGHT = 136;
	private static final int PANEL_ROW_X = 10;
	private static final int PANEL_ROW_Y = 42;
	private static final int PANEL_ROW_SPACING = 18;
	private static final float PANEL_TEXT_SCALE = 0.75F;
	private static final List<EnhancementType> CORE_TYPES = List.of(EnhancementType.values());
	private boolean compatibilityPanelOpen;

	public EnhancementWorkbenchScreen(
			EnhancementWorkbenchMenu menu,
			Inventory inventory,
			Component title
	) {
		super(menu, inventory, title);
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
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

		boolean infoHovered = isWithinInfoButton(mouseX, mouseY);
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
			graphics.setTooltipForNextFrame(
					Component.translatable(compatibilityPanelOpen
							? "gui.justexcavators.enhancement_workbench.compatibility.close"
							: "gui.justexcavators.enhancement_workbench.compatibility.open"),
					mouseX,
					mouseY
			);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		if (compatibilityPanelOpen) {
			renderCompatibilityPanel(graphics, mouseX, mouseY);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		int titleWidth = font.width(title);
		float titleScale = Math.min(
				TITLE_SCALE,
				TITLE_MAX_WIDTH / (float) Math.max(titleWidth, 1)
		);
		graphics.pose().pushMatrix();
		graphics.pose().translate(TITLE_CENTER_X, TITLE_Y);
		graphics.pose().scale(titleScale);
		graphics.drawString(font, title, -titleWidth / 2, 0, TITLE_COLOR, false);
		graphics.pose().popMatrix();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() == InputConstants.MOUSE_BUTTON_LEFT
				&& isWithinInfoButton(event.x(), event.y())) {
			compatibilityPanelOpen = !compatibilityPanelOpen;
			AbstractWidget.playButtonClickSound(minecraft.getSoundManager());
			return true;
		}
		if (compatibilityPanelOpen) {
			if (!isWithinCompatibilityPanel(event.x(), event.y())) {
				compatibilityPanelOpen = false;
			}
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (compatibilityPanelOpen) {
			if (event.key() == InputConstants.KEY_ESCAPE) {
				compatibilityPanelOpen = false;
			}
			return true;
		}
		return super.keyPressed(event);
	}

	private void renderCompatibilityPanel(GuiGraphics graphics, int mouseX, int mouseY) {
		int panelLeft = leftPos + PANEL_X;
		int panelTop = topPos + PANEL_Y;
		drawVanillaPanel(graphics, panelLeft, panelTop, PANEL_WIDTH, PANEL_HEIGHT);
		drawCenteredPanelText(
				graphics,
				Component.translatable("gui.justexcavators.enhancement_workbench.compatibility.title"),
				panelLeft + PANEL_WIDTH / 2,
				panelTop + 7
		);

		ItemStack hoveredCore = ItemStack.EMPTY;
		for (int row = 0; row < CORE_TYPES.size(); row++) {
			EnhancementType source = CORE_TYPES.get(row);
			int itemY = topPos + PANEL_ROW_Y + row * PANEL_ROW_SPACING;
			int sourceX = leftPos + PANEL_ROW_X;
			hoveredCore = renderCore(graphics, source, sourceX, itemY, mouseX, mouseY, hoveredCore);
			graphics.drawString(font, "→", sourceX + 22, itemY + 4, TITLE_COLOR, false);

			int compatibleX = sourceX + 36;
			for (EnhancementType candidate : CORE_TYPES) {
				if (EnhancementCompatibility.isValidPair(source, candidate)) {
					hoveredCore = renderCore(
							graphics,
							candidate,
							compatibleX,
							itemY,
							mouseX,
							mouseY,
							hoveredCore
					);
					compatibleX += PANEL_ROW_SPACING;
				}
			}
		}

		drawCenteredPanelText(
				graphics,
				Component.translatable("gui.justexcavators.enhancement_workbench.compatibility.no_duplicates"),
				panelLeft + PANEL_WIDTH / 2,
				panelTop + PANEL_HEIGHT - 12
		);
		if (!hoveredCore.isEmpty()) {
			graphics.setTooltipForNextFrame(font, hoveredCore, mouseX, mouseY);
		}
	}

	private static ItemStack renderCore(
			GuiGraphics graphics,
			EnhancementType type,
			int x,
			int y,
			int mouseX,
			int mouseY,
			ItemStack hoveredCore
	) {
		drawVanillaSlot(graphics, x, y);
		ItemStack stack = EnhancementCoreCatalog.stackOf(type);
		graphics.renderItem(stack, x, y);
		return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16
				? stack
				: hoveredCore;
	}

	private static void drawVanillaPanel(
			GuiGraphics graphics,
			int x,
			int y,
			int width,
			int height
	) {
		graphics.fill(x, y, x + width, y + height, 0xFF373737);
		graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);
		graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFFC6C6C6);
	}

	private static void drawVanillaSlot(GuiGraphics graphics, int x, int y) {
		graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFFFFFFFF);
		graphics.fill(x - 1, y - 1, x + 16, y + 16, 0xFF373737);
		graphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
	}

	private void drawCenteredPanelText(
			GuiGraphics graphics,
			Component text,
			int centerX,
			int y
	) {
		int textWidth = font.width(text);
		graphics.pose().pushMatrix();
		graphics.pose().translate(centerX, y);
		graphics.pose().scale(PANEL_TEXT_SCALE);
		graphics.drawString(font, text, -textWidth / 2, 0, TITLE_COLOR, false);
		graphics.pose().popMatrix();
	}

	private boolean isWithinInfoButton(double mouseX, double mouseY) {
		return mouseX >= leftPos + INFO_X
				&& mouseX < leftPos + INFO_X + INFO_SIZE
				&& mouseY >= topPos + INFO_Y
				&& mouseY < topPos + INFO_Y + INFO_SIZE;
	}

	private boolean isWithinCompatibilityPanel(double mouseX, double mouseY) {
		return mouseX >= leftPos + PANEL_X
				&& mouseX < leftPos + PANEL_X + PANEL_WIDTH
				&& mouseY >= topPos + PANEL_Y
				&& mouseY < topPos + PANEL_Y + PANEL_HEIGHT;
	}
}
