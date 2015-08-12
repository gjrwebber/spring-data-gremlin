package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * A {@link GremlinFieldPropertyAccessor} for IDs.
 *
 * @author Gman
 */
public class GremlinIdFieldPropertyAccessor extends GremlinFieldPropertyAccessor<String> {


    public GremlinIdFieldPropertyAccessor(Field field) {
        super(field);
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
    public void set(Object object, String val) {
        try {
            field.set(object, val);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
