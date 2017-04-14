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

    protected AbstractGremlinFieldPropertyAccessor embeddedAccessor;

    public AbstractGremlinFieldPropertyAccessor(Field field, AbstractGremlinFieldPropertyAccessor embeddedAccessor) {
        this(field);
        this.embeddedAccessor = embeddedAccessor;
    }

    public AbstractGremlinFieldPropertyAccessor(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    protected Object getEmbeddedObject(Object object, boolean force) {

        if (embeddedAccessor != null) {
            Object parentObj = embeddedAccessor.get(object);
            if (parentObj == null) {
                if (force) {
                    parentObj = embeddedAccessor.newInstance();
                    embeddedAccessor.set(object, parentObj);
                }
            }
            object = parentObj;
        }

        return object;
    }

    public Object newInstance() {
        try {
            return field.getType().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create a new instance of " + field.getType() + ": " + e.getMessage(), e);
        }
    }

    /**
     * @return the root Field of this GremlinEmbeddedFieldAccessor
     */
    public Field getRootField() {
        AbstractGremlinFieldPropertyAccessor rootFieldAccessor = this;
        AbstractGremlinFieldPropertyAccessor embeddedFieldAccessor = this.getEmbeddedAccessor();
        while (embeddedFieldAccessor != null) {
            rootFieldAccessor = embeddedFieldAccessor;
            embeddedFieldAccessor = rootFieldAccessor.getEmbeddedAccessor();
        }
        return rootFieldAccessor.getField();
    }

    public AbstractGremlinFieldPropertyAccessor getEmbeddedAccessor() {
        return embeddedAccessor;
    }

    @Override
    public Field getField() {
        return field;
    }
}