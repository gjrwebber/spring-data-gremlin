package org.springframework.data.gremlin.utils;

import java.lang.reflect.*;

/**
 * A utility class for finding the generic types of Classes, Fields and Methods.
 *
 * @author Gman
 */
public class GenericsUtil {

    public static Class<?>[] getGenericTypes(Type type, int required) {

        while (type != null && type instanceof Class<?>) {
            type = ((Class<?>) type).getGenericSuperclass();
        }

        if (type != null && type instanceof TypeVariable) {
            return new Class<?>[]{ TypeVariable.class };
        }

        if (type != null && type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] genericTypes = paramType.getActualTypeArguments();
            if (genericTypes == null || (required > 0 && genericTypes.length != required)) {
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
                } else if (genericTypes[i] instanceof TypeVariable) {
                    String upperBoundTypeName = ((TypeVariable)genericTypes[i]).getBounds()[0].getTypeName();
                    if(!upperBoundTypeName.equals(Object.class.getCanonicalName())) {
                        try {
                            generics[i] = Class.forName(upperBoundTypeName);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        generics[i] = TypeVariable.class;
                    }
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
        Class<?> cls = getGenericTypes(clazz, 1)[0];
        if (cls == TypeVariable.class) {
            throw new TypeVariableException();
        } else {
            return cls;
        }
    }


    public static Class<?>[] getGenericTypes(Field field, int required) {
        Type type = field.getGenericType();
        return getGenericTypes(type, required);
    }

    public static Class<?> getGenericType(Field field) {
        Class<?> cls = getGenericTypes(field, 1)[0];
        if (cls == TypeVariable.class) {
            throw new TypeVariableException();
        } else {
            return cls;
        }
    }

    public static Class<?>[] getGenericTypes(Method method, int required) {
        Type type = method.getGenericReturnType();
        return getGenericTypes(type, required);
    }

    public static Class<?> getGenericType(Method method) {
        Class<?> cls = getGenericTypes(method, 1)[0];
        if (cls == TypeVariable.class) {
            throw new TypeVariableException();
        } else {
            return cls;
        }
    }

    public static Class<?> getGenericType(Field field, Class<?> owningClass) {
        Class<?> cls = getGenericTypes(field, 1)[0];
        if (cls == TypeVariable.class) {
            Type var = field.getGenericType();
            if (var instanceof ParameterizedType) {
                var = ((ParameterizedType) var).getActualTypeArguments()[0];
            }
            int index = 0;
            for (Type stype : field.getDeclaringClass().getTypeParameters()) {
                if (stype instanceof TypeVariable) {
                    if (stype.getTypeName().equals(var.getTypeName())) {
                        cls = GenericsUtil.getGenericTypes(owningClass, -1)[index];
                        break;
                    }
                }

            }
        }

        return cls;
    }

    public static class TypeVariableException extends RuntimeException { }
}
