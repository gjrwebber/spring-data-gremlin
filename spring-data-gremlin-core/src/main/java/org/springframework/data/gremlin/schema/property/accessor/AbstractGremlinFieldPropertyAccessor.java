package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public abstract class AbstractGremlinFieldPropertyAccessor<V> implements GremlinPropertyAccessor<V> {

    protected Field field;

    public AbstractGremlinFieldPropertyAccessor(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }
}
