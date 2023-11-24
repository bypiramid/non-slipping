package net.bypiramid.reflectionapi.util;

import java.lang.reflect.*;

/**
 * Helper class to set fields, methods &amp; constructors accessible
 */
public abstract class AccessUtil {

    public static final boolean VERBOSE = false;

    private static final Field modifiersField;

    /**
     * Sets the field accessible and removes final modifiers
     *
     * @param field Field to set accessible
     * @return the Field
     * @throws ReflectiveOperationException (usually never)
     */

    public static Field setAccessible(Field field) throws ReflectiveOperationException {
        return setAccessible(field, false);
    }

    public static Field setAccessible(Field field, boolean readOnly) throws ReflectiveOperationException {
        return setAccessible(field, readOnly, false);
    }

    private static Field setAccessible(Field field, boolean readOnly, boolean privileged) throws ReflectiveOperationException {
        field.setAccessible(true);
        if (readOnly) {
            return field;
        }
        removeFinal(field, privileged);
        return field;
    }

    private static void removeFinal(Field field, boolean privileged) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            try {
                removeFinalSimple(field);
            } catch (Exception e1) {
                if (VERBOSE) {
                    System.err.println("removeFinalSimple");
                    e1.printStackTrace();
                }
            }
            if (VERBOSE && Modifier.isFinal(field.getModifiers())) {
                System.err.println("Failed to make " + field + " non-final");
            }
        }
    }

    private static void removeFinalSimple(Field field) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
    }

    private static void removeFinalVarHandle(Field field) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();
        int newModifiers = modifiers & ~Modifier.FINAL;
        modifiersField.setInt(field, newModifiers);
    }

    private static void removeFinalNativeDeclaredFields(Field field) throws ReflectiveOperationException {
        removeFinalNativeDeclaredFields(field, false);
    }

    private static void removeFinalNativeDeclaredFields(Field field, boolean secondTry) throws ReflectiveOperationException {
        int modifiers = field.getModifiers();
        // https://github.com/ViaVersion/ViaVersion/blob/e07c994ddc50e00b53b728d08ab044e66c35c30f/bungee/src/main/java/us/myles/ViaVersion/bungee/platform/BungeeViaInjector.java
        // Java 12 compatibility *this is fine*
        Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);
        Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
        for (Field classField : fields) {
            if ("modifiers".equals(classField.getName())) {
                classField.setAccessible(true);
                classField.set(field, modifiers & ~Modifier.FINAL);
                break;
            }
        }
    }

    /**
     * Sets the method accessible
     *
     * @param method Method to set accessible
     * @return the Method
     * @throws ReflectiveOperationException (usually never)
     */
    public static Method setAccessible(Method method) throws ReflectiveOperationException {
        method.setAccessible(true);
        return method;
    }

    /**
     * Sets the constructor accessible
     *
     * @param constructor Constructor to set accessible
     * @return the Constructor
     * @throws ReflectiveOperationException (usually never)
     */
    public static Constructor setAccessible(Constructor constructor) throws ReflectiveOperationException {
        constructor.setAccessible(true);
        return constructor;
    }

    private static Field initModifiersField() {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            return modifiersField;
        } catch (NoSuchFieldException ignored) {}
        return null;
    }

    static {
        modifiersField = initModifiersField();
    }
}
