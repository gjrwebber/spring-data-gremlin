package org.springframework.data.gremlin.utils;

import java.lang.reflect.*;

/**
 * A utility class for finding the generic types of Classes, Fields and Methods.
 *
 * @author Gman
 */
public class GenericsUtil {

    public static Class<?>[] getGenericTypes(Type type, int required) {

        if (type != null && type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] genericTypes = paramType.getActualTypeArguments();
            if (genericTypes == null || genericTypes.length != required) {
                throw new IllegalStateException(type.getTypeName() + " does not provide a generic type as required. Wanted " + required + " generic types, but found " + genericTypes.length);
            }
            Class<?>[] generics = new Class<?>[genericTypes.length];
            for (int i = 0; i < generics.length; i++) {
                if (genericTypes[i] instanceof ParameterizedType) {
                    return getGenericTypes(genericTypes[i], required);
                } else if (genericTypes[i] instanceof GenericArrayType) {
                    GenericArrayType arrayType = (GenericArrayType) genericTypes[i];
                    Class<?> arrayClassType = (Class<?>) arrayType.getGenericComponentType();
                    generics[i] = Array.newInstance(arrayClassType, 1).getClass();
                } else {
                    generics[i] = (Class<?>) genericTypes[i];
                }
            }
            return generics;
        }
        throw new IllegalStateException("Could not determine generic types for " + type.getTypeName());
    }

    /**
     * Returns the generic types of the given {@link Class} or throws an
     * {@link IllegalStateException} if none are available
     *
     * @param clazz    The {@link Class} to inspect.
     * @param required The number of required types. If this number of generics are
     *                 not found an {@link IllegalStateException} is thrown
     * @return Returns the generic types of the given {@link Class} or throws an
     * {@link IllegalStateException} if none are available or null if
     * none are available
     * @throws IllegalStateException If the given number of required generics aren't found. Or
     *                               generic type could not be determined.
     */
    public static <T> Class<?>[] getGenericTypes(Class<T> clazz, int required) {
        Type type = clazz.getGenericSuperclass();

        while (type != null && type instanceof Class<?>) {
            type = ((Class<?>) type).getGenericSuperclass();
        }
        return getGenericTypes(type, required);
    }

    /**
     * Returns the generic type of the given {@link Class} or throws an
     * {@link IllegalStateException} if none are available
     *
     * @param clazz The {@link Class} to inspect.
     * @return Returns the generic type of the given {@link Class} or throws an
     * {@link IllegalStateException} if none are available
     * @throws IllegalStateException If no generic type is found. Or generic type could not be
     *                               determined.
     */
    public static <T> Class<?> getGenericType(Class<T> clazz) {
        return getGenericTypes(clazz, 1)[0];
    }


    public static Class<?>[] getGenericTypes(Field field, int required) {
        Type type = field.getGenericType();
        return getGenericTypes(type, required);
    }

    public static Class<?> getGenericType(Field field) {
        return getGenericTypes(field, 1)[0];
    }

    public static Class<?>[] getGenericTypes(Method method, int required) {
        Type type = method.getGenericReturnType();
        return getGenericTypes(type, required);
    }

    public static Class<?> getGenericType(Method method) {
        return getGenericTypes(method, 1)[0];
    }
}
