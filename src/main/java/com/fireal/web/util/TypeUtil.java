package com.fireal.web.util;

public class TypeUtil {

    public static boolean canCast(Class<?> targetType) {
        if (targetType.isPrimitive()) return true;
        return targetType == String.class || targetType == Integer.class || targetType == Double.class
                || targetType == Float.class || targetType == Long.class || targetType == Short.class
                || targetType == Byte.class || targetType == Boolean.class || targetType == Character.class;
    }

    public static Object castString(String str, Class<?> targetType) {
        if (targetType == String.class) return str;
        try {
            if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(str);
            if (targetType == double.class || targetType == Double.class) return Double.parseDouble(str);
            if (targetType == float.class || targetType == Float.class) return Float.parseFloat(str);
            if (targetType == long.class || targetType == Long.class) return Long.parseLong(str);
            if (targetType == short.class || targetType == Short.class) return Short.parseShort(str);
            if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            if (str.equals("true") || str.equals("false")) {
                return Boolean.parseBoolean(str);
            }
            return null;
        }
        if (targetType == char.class || targetType == Character.class) return str.charAt(0);
        return null;
    }

}
