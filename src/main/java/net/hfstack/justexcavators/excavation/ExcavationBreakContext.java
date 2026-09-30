package net.hfstack.justexcavators.excavation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.hfstack.justexcavators.enhancement.ActiveEnhancements;

public record ExcavationBreakContext(
		ServerPlayer player,
		ItemStack originalTool,
		ActiveEnhancements enhancements,
		BlockPos origin,
		BlockState centralState,
		boolean additional
) {
	private static final ThreadLocal<Deque<Scope>> CONTEXTS = new ThreadLocal<>();

	public static Scope open(
			ServerPlayer player,
			ItemStack originalTool,
			ActiveEnhancements enhancements,
			BlockPos origin,
			BlockState centralState,
			boolean additional
	) {
		return open(new ExcavationBreakContext(
				player, originalTool, enhancements, origin, centralState, additional
		));
	}

	static Scope open(ExcavationBreakContext context) {
		Deque<Scope> stack = CONTEXTS.get();
		if (stack == null) {
			stack = new ArrayDeque<>();
			CONTEXTS.set(stack);
		}
		Scope scope = new Scope(context);
		stack.push(scope);
		return scope;
	}

	public static Optional<ExcavationBreakContext> current() {
		Deque<Scope> stack = CONTEXTS.get();
		return stack == null || stack.isEmpty()
				? Optional.empty()
				: Optional.of(stack.peek().context);
	}

	public static final class Scope implements AutoCloseable {
		private final ExcavationBreakContext context;
		private boolean closed;

		private Scope(ExcavationBreakContext context) {
			this.context = context;
		}

		@Override
		public void close() {
			if (closed) {
				throw new IllegalStateException("Enhancement break scope is already closed");
			}
			Deque<Scope> stack = CONTEXTS.get();
			if (stack == null || stack.peek() != this) {
				throw new IllegalStateException("Enhancement break scopes must close in LIFO order");
			}
			stack.pop();
			closed = true;
			if (stack.isEmpty()) {
				CONTEXTS.remove();
			}
		}
	}
}
