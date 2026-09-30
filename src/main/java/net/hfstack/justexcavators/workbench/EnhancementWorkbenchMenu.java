package net.hfstack.justexcavators.workbench;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import net.hfstack.justexcavators.block.ModBlocks;
import net.hfstack.justexcavators.component.ExcavatorComponents;
import net.hfstack.justexcavators.component.ExcavatorEnhancements;
import net.hfstack.justexcavators.enhancement.EnhancementCoreCatalog;
import net.hfstack.justexcavators.enhancement.EnhancementType;
import net.hfstack.justexcavators.item.ExcavatorItem;
import net.hfstack.justexcavators.item.LegacySilkMigration;
import net.hfstack.justexcavators.workbench.EnhancementWorkbenchTransactions.CoreClickResult;

public final class EnhancementWorkbenchMenu extends AbstractContainerMenu {
	public static final int TOOL_SLOT = 0;
	public static final int CORE_SLOT_1 = 1;
	public static final int CORE_SLOT_2 = 2;
	public static final int PLAYER_INVENTORY_START = 3;
	private static final int PLAYER_MAIN_END = PLAYER_INVENTORY_START + 27;
	private static final int PLAYER_END = PLAYER_MAIN_END + 9;

	private final ContainerLevelAccess access;
	private final Inventory playerInventory;
	private final SimpleContainer tool = new SimpleContainer(1);
	private final SimpleContainer projections = new SimpleContainer(2);
	private boolean refreshing;

	public EnhancementWorkbenchMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, ContainerLevelAccess.NULL);
	}

	public EnhancementWorkbenchMenu(
			int containerId,
			Inventory inventory,
			ContainerLevelAccess access
	) {
		super(ModMenuTypes.ENHANCEMENT_WORKBENCH, containerId);
		this.access = access;
		this.playerInventory = inventory;

		addSlot(new Slot(tool, 0, 114, 50) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof ExcavatorItem;
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		addSlot(coreSlot(0, 74, 97));
		addSlot(coreSlot(1, 154, 97));

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlot(new Slot(inventory, column + row * 9 + 9, 14 + column * 24, 147 + row * 24));
			}
		}
		for (int column = 0; column < 9; column++) {
			addSlot(new Slot(inventory, column, 14 + column * 24, 223));
		}
	}

	private Slot coreSlot(int componentSlot, int x, int y) {
		return new Slot(projections, componentSlot, x, y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				Optional<EnhancementType> type = EnhancementCoreCatalog.typeOf(stack);
				if (type.isEmpty() || !hasTool()) {
					return false;
				}
				return EnhancementWorkbenchTransactions.click(
						currentEnhancements(),
						componentSlot,
						type,
						stack.getCount(),
						true,
						hasEnchantment(Enchantments.SILK_TOUCH),
						hasEnchantment(Enchantments.FORTUNE)
				).isPresent();
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}

			@Override
			public boolean isActive() {
				return hasTool();
			}

			@Override
			public boolean isFake() {
				return true;
			}
		};
	}

	@Override
	public void clicked(int slotId, int button, ContainerInput input, Player player) {
		if (slotId == CORE_SLOT_1 || slotId == CORE_SLOT_2) {
			if (input == ContainerInput.QUICK_MOVE) {
				super.clicked(slotId, button, input, player);
			} else if (EnhancementWorkbenchTransactions.acceptsCoreInput(input)
					&& !player.level().isClientSide()) {
				handleCoreClick(slotId - CORE_SLOT_1, player);
			}
			return;
		}
		super.clicked(slotId, button, input, player);
		refreshProjections();
	}

	@Override
	public boolean canDragTo(Slot slot) {
		return slot.container != projections && super.canDragTo(slot);
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != projections && super.canTakeItemForPickAll(stack, slot);
	}

	private void handleCoreClick(int slot, Player player) {
		if (!hasTool()) {
			return;
		}
		ItemStack carried = getCarried();
		Optional<EnhancementType> carriedType = carried.isEmpty()
				? Optional.empty()
				: EnhancementCoreCatalog.typeOf(carried);
		if (!carried.isEmpty() && carriedType.isEmpty()) {
			return;
		}

		Optional<CoreClickResult> transaction = EnhancementWorkbenchTransactions.click(
				currentEnhancements(),
				slot,
				carriedType,
				carried.getCount(),
				canInventoryAccept(player, currentEnhancements().slot(slot)),
				hasEnchantment(Enchantments.SILK_TOUCH),
				hasEnchantment(Enchantments.FORTUNE)
		);
		transaction.ifPresent(result -> apply(result, player));
	}

	private boolean canInventoryAccept(Player player, Optional<EnhancementType> outgoing) {
		return outgoing.isEmpty() || canFullyAdd(player.getInventory(),
				EnhancementCoreCatalog.stackOf(outgoing.orElseThrow())
		);
	}

	private void apply(CoreClickResult result, Player player) {
		ItemStack carried = getCarried();
		if (result.consumedFromCursor() > 0) {
			carried.shrink(result.consumedFromCursor());
		}
		result.cursorReturn().ifPresent(type -> setCarried(EnhancementCoreCatalog.stackOf(type)));
		result.inventoryReturn().ifPresent(type -> player.getInventory().add(
				EnhancementCoreCatalog.stackOf(type)
		));
		setEnhancements(result.enhancements());
	}

	private void setEnhancements(ExcavatorEnhancements enhancements) {
		ItemStack stack = tool.getItem(0);
		stack.set(ExcavatorComponents.ENHANCEMENTS, enhancements);
		tool.setChanged();
		refreshProjections();
		broadcastChanges();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		if (player.level().isClientSide()) {
			return ItemStack.EMPTY;
		}
		if (index < 0 || index >= slots.size()) {
			return ItemStack.EMPTY;
		}
		Slot source = slots.get(index);
		if (!source.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack original = source.getItem().copy();

		if (index == CORE_SLOT_1 || index == CORE_SLOT_2) {
			Optional<CoreClickResult> removal = EnhancementWorkbenchTransactions.click(
					currentEnhancements(), index - CORE_SLOT_1, Optional.empty(), 0, true,
					hasEnchantment(Enchantments.SILK_TOUCH), hasEnchantment(Enchantments.FORTUNE)
			);
			if (removal.isEmpty() || !canFullyAdd(player.getInventory(), original)) {
				return ItemStack.EMPTY;
			}
			player.getInventory().add(original.copy());
			setEnhancements(removal.orElseThrow().enhancements());
			return original;
		}

		if (index == TOOL_SLOT) {
			if (!moveItemStackTo(source.getItem(), PLAYER_INVENTORY_START, PLAYER_END, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index >= PLAYER_INVENTORY_START) {
			ItemStack stack = source.getItem();
			if (stack.getItem() instanceof ExcavatorItem && !slots.get(TOOL_SLOT).hasItem()) {
				if (!moveItemStackTo(stack, TOOL_SLOT, TOOL_SLOT + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				Optional<EnhancementType> type = EnhancementCoreCatalog.typeOf(stack);
				OptionalInt target = type.isEmpty() || !hasTool() ? OptionalInt.empty()
						: EnhancementWorkbenchTransactions.firstQuickInstallSlot(
								currentEnhancements(), type.orElseThrow(),
								hasEnchantment(Enchantments.SILK_TOUCH), hasEnchantment(Enchantments.FORTUNE)
						);
				if (target.isPresent()) {
					ExcavatorEnhancements before = currentEnhancements();
					stack.shrink(1);
					setEnhancements(before.withSlot(target.getAsInt(), type.orElseThrow()));
				} else if (index < PLAYER_MAIN_END) {
					if (!moveItemStackTo(stack, PLAYER_MAIN_END, PLAYER_END, false)) return ItemStack.EMPTY;
				} else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_MAIN_END, false)) {
					return ItemStack.EMPTY;
				}
			}
		} else {
			return ItemStack.EMPTY;
		}

		if (source.getItem().isEmpty()) source.setByPlayer(ItemStack.EMPTY);
		else source.setChanged();
		refreshProjections();
		return original;
	}

	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);
		if (!refreshing) refreshProjections();
	}

	private void refreshProjections() {
		refreshing = true;
		try {
			if (hasTool()) {
				LegacySilkMigration.migrate(tool.getItem(0), ExcavatorComponents.ENHANCEMENTS);
			}
			EnhancementWorkbenchProjection<ItemStack> projected = EnhancementWorkbenchProjection.from(
					currentEnhancements(),
					EnhancementCoreCatalog::stackOf
			);
			for (int slot = 0; slot < 2; slot++) {
				projections.setItem(slot, projected.slot(slot).orElse(ItemStack.EMPTY));
			}
		} finally {
			refreshing = false;
		}
	}

	private boolean hasTool() {
		return tool.getItem(0).getItem() instanceof ExcavatorItem;
	}

	private static boolean canFullyAdd(Inventory inventory, ItemStack incoming) {
		int remaining = incoming.getCount();
		for (ItemStack present : inventory.getNonEquipmentItems()) {
			if (present.isEmpty()) {
				remaining -= incoming.getMaxStackSize();
			} else if (ItemStack.isSameItemSameComponents(present, incoming)) {
				remaining -= Math.max(0, present.getMaxStackSize() - present.getCount());
			}
			if (remaining <= 0) {
				return true;
			}
		}
		return false;
	}

	private ExcavatorEnhancements currentEnhancements() {
		return hasTool() ? tool.getItem(0).getOrDefault(
				ExcavatorComponents.ENHANCEMENTS,
				ExcavatorEnhancements.EMPTY
		) : ExcavatorEnhancements.EMPTY;
	}

	private boolean hasEnchantment(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key) {
		return hasTool() && tool.getItem(0).getEnchantments().keySet().stream().anyMatch(holder -> holder.is(key));
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(access, player, ModBlocks.ENHANCEMENT_WORKBENCH);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		projections.clearContent();
		clearContainer(player, tool);
	}
}
