package net.hfstack.justexcavators.mixin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

class ServerPlayerGameModeMixinTargetTest {
	private static final Pattern FIELD_TARGET = Pattern.compile("L([^;]+);([^:]+):(.+)");
	private static final Pattern METHOD_TARGET = Pattern.compile("L([^;]+);([^\\(]+)(\\(.+)");
	private static final String TARGET_CLASS = "/net/minecraft/server/level/ServerPlayerGameMode.class";

	@Test
	void acceptedHitInjectionMatchesOneFieldWriteInTargetMethod() throws IOException {
		Method handler = findMethod("justexcavators$trackAcceptedHit");
		Inject injection = handler.getAnnotation(Inject.class);
		At fieldInjection = injection.at()[0];
		Matcher target = FIELD_TARGET.matcher(fieldInjection.target());

		if (!target.matches()) {
			throw new AssertionError("Expected a JVM field target, got: " + fieldInjection.target());
		}

		String owner = target.group(1);
		String fieldName = target.group(2);
		String fieldDescriptor = target.group(3);
		AtomicInteger matches = new AtomicInteger();
		String classResource = "/" + owner + ".class";

		try (InputStream bytecode = ServerPlayerGameModeMixinTargetTest.class.getResourceAsStream(classResource)) {
			if (bytecode == null) {
				throw new AssertionError("Missing Minecraft target class: " + classResource);
			}
			new ClassReader(bytecode).accept(new ClassVisitor(Opcodes.ASM9) {
				@Override
				public MethodVisitor visitMethod(
						int access,
						String name,
						String descriptor,
						String signature,
						String[] exceptions
				) {
					if (!name.equals(injection.method()[0])) {
						return null;
					}
					return new MethodVisitor(Opcodes.ASM9) {
						@Override
						public void visitFieldInsn(int opcode, String actualOwner, String actualName, String actualDescriptor) {
							if (opcode == fieldInjection.opcode()
									&& actualOwner.equals(owner)
									&& actualName.equals(fieldName)
									&& actualDescriptor.equals(fieldDescriptor)) {
								matches.incrementAndGet();
							}
						}
					};
				}
			}, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		}

		assertEquals(1, matches.get(), "The required Mixin field injection must match exactly once");
	}

	@Test
	void centralBreakWrapMatchesOneInvocationInTargetMethod() throws IOException {
		Method handler = findMethod("justexcavators$scopeCentralBreak");
		WrapOperation wrap = handler.getAnnotation(WrapOperation.class);
		At invocation = wrap.at()[0];
		Matcher target = METHOD_TARGET.matcher(invocation.target());

		if (!target.matches()) {
			throw new AssertionError("Expected a JVM method target, got: " + invocation.target());
		}

		String owner = target.group(1);
		String methodName = target.group(2);
		String methodDescriptor = target.group(3);
		AtomicInteger matches = new AtomicInteger();

		try (InputStream bytecode = ServerPlayerGameModeMixinTargetTest.class.getResourceAsStream(TARGET_CLASS)) {
			if (bytecode == null) {
				throw new AssertionError("Missing Minecraft target class: " + TARGET_CLASS);
			}
			new ClassReader(bytecode).accept(new ClassVisitor(Opcodes.ASM9) {
				@Override
				public MethodVisitor visitMethod(
						int access,
						String name,
						String descriptor,
						String signature,
						String[] exceptions
				) {
					if (!name.equals(wrap.method()[0])) {
						return null;
					}
					return new MethodVisitor(Opcodes.ASM9) {
						@Override
						public void visitMethodInsn(
								int opcode,
								String actualOwner,
								String actualName,
								String actualDescriptor,
								boolean isInterface
						) {
							if (actualOwner.equals(owner)
									&& actualName.equals(methodName)
									&& actualDescriptor.equals(methodDescriptor)) {
								matches.incrementAndGet();
							}
						}
					};
				}
			}, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		}

		assertEquals(1, matches.get(), "The required Mixin invocation must match exactly once");
	}

	private static Method findMethod(String name) {
		for (Method method : ServerPlayerGameModeMixin.class.getDeclaredMethods()) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		throw new AssertionError("Missing Mixin handler: " + name);
	}
}
