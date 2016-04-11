package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public abstract class AbstractGremlinFieldPropertyAccessor<V> extends AbstractEmbeddableGremlinPropertyAccessor<V> implements GremlinPropertyFieldAccessor<V> {

    protected Field field;

    public AbstractGremlinFieldPropertyAccessor(Field field, AbstractGremlinFieldPropertyAccessor embeddedAccessor) {
        super(embeddedAccessor);
        field.setAccessible(true);
        this.field = field;
    }

    public AbstractGremlinFieldPropertyAccessor(Field field) {
        this(field, null);
    }

    public Object newInstance() {
        try {
            return field.getType().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create a new instance of " + field.getType() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Field getField() {
        return field;
    }
}
