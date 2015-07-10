package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for enum properties that will be mapped with the name (String).
 *
 * @author Gman
 */
public class GremlinIdFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<String> {

    protected Class<? extends Enum> numnum;

    public GremlinIdFieldPropertyAccessor(Field field, Class<?> numnum) {
        super(field);
        this.numnum = (Class<? extends Enum>) numnum;
    }

    @Override
    public String get(Object object) {

        try {
            Object result = field.get(object);

            if (result == null) {
                return null;
            }
            return result.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, String name) {
        try {
            if (name == null) {
                field.set(object, name);
            }
            for (Enum num : numnum.getEnumConstants()) {
                if (num.name().equals(name)) {
                    field.set(object, num);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
