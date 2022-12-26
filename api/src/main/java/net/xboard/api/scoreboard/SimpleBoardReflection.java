package net.xboard.api.scoreboard;

import fr.mrmicky.fastboard.FastReflection;
import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

public class SimpleBoardReflection {
	public static final String OBC_PACKAGE = "org.bukkit.craftbukkit";
	public static final String VERSION = Bukkit.getServer()
		 .getClass()
		 .getPackage()
		 .getName()
		 .substring(OBC_PACKAGE.length() + 1);
	
	private static final String NM_PACKAGE = "net.minecraft";
	private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);
	private static final boolean NMS_REPACKAGED = optionalClass(NM_PACKAGE + ".network.protocol.Packet").isPresent();
	
	private static volatile Object theUnsafe;
	
	private SimpleBoardReflection() {
		throw new UnsupportedOperationException();
	}
	
	public static boolean isRepackaged() {
		return NMS_REPACKAGED;
	}
	
	public static String nmsClassName(String post1_17package, String className) {
		if (NMS_REPACKAGED) return post1_17package == null ? NM_PACKAGE : NM_PACKAGE + '.' + post1_17package + '.' + className;
		
		return NM_PACKAGE + ".server" + '.' + VERSION + '.' + className;
	}
	
	public static Class<?> nmsClass(String post1_17package, String className) throws ClassNotFoundException {
		return Class.forName(nmsClassName(post1_17package, className));
	}
	
	public static Optional<Class<?>> nmsOptionalClass(String post1_17package, String className) {
		return optionalClass(nmsClassName(post1_17package, className));
	}
	
	public static String obcClassName(String className) {
		return OBC_PACKAGE + '.' + VERSION + '.' + className;
	}
	
	public static Class<?> obcClass(String className) throws ClassNotFoundException {
		return Class.forName(obcClassName(className));
	}
	
	public static Optional<Class<?>> optionalClass(String className) {
		try { return Optional.of(Class.forName(className)); }
		catch (ClassNotFoundException exception) { return Optional.empty(); }
	}
	
	public static Object enumValueOf(Class<?> enumClass, String enumName) {
		return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
	}
	
	public static Object enumValueOf(Class<?> enumClass, String enumName, int fallbackOrdinal) {
		try { return enumValueOf(enumClass, enumName); }
		catch (IllegalArgumentException exception) {
			Object[] constants = enumClass.getEnumConstants();
			if (constants.length > fallbackOrdinal) return constants[fallbackOrdinal];
			
			throw exception;
		}
	}
	
	static Class<?> innerClass(Class<?> parentClass, Predicate<Class<?>> classPredicate) throws ClassNotFoundException {
		for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
			if (classPredicate.test(innerClass)) return innerClass;
		}
		
		throw new ClassNotFoundException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
	}
	
	public static SimpleBoardReflection.PacketConstructor findPacketConstructor(Class<?> packetClass, MethodHandles.Lookup lookup) throws Exception {
		try { return lookup.findConstructor(packetClass, VOID_METHOD_TYPE)::invoke; }
		catch (NoSuchMethodException | IllegalAccessException exception) {
			// try below with Unsafe
		}
		
		if (theUnsafe == null) {
			synchronized (FastReflection.class) {
				if (theUnsafe == null) {
					Field theUnsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
					theUnsafeField.setAccessible(true);
					
					theUnsafe = theUnsafeField.get(null);
				}
			}
		}
		
		MethodHandle allocateMethod = lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", MethodType.methodType(Object.class, Class.class));
		return () -> allocateMethod.invoke(theUnsafe, packetClass);
	}
	
	@FunctionalInterface
	interface PacketConstructor {
		Object invoke() throws Throwable;
	}
}
