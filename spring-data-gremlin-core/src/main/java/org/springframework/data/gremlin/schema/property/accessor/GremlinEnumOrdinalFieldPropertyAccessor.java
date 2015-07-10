package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for enum properties that will be mapped with the ordinal (integer).
 *
 * @author Gman
 */
public class GremlinEnumOrdinalFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<Integer> {
    private Class<? extends Enum> numnum;

    public GremlinEnumOrdinalFieldPropertyAccessor(Field field, Class<?> numnum) {
        super(field);
        this.numnum = (Class<? extends Enum>) numnum;
    }

    @Override
    public Integer get(Object object) {
        try {
            Object result = field.get(object);
            if (result == null) {
                return null;
            }
            Enum numnum = (Enum) result;
            return numnum.ordinal();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, Integer ordinal) {
        try {
            if (ordinal == null) {
                field.set(object, null);
                return;
            }
            field.set(object, numnum.getEnumConstants()[ordinal]);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
