package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public abstract class AbstractEmbeddableGremlinPropertyAccessor<V> implements GremlinPropertyAccessor<V> {

    protected AbstractGremlinFieldPropertyAccessor embeddedAccessor;

    public AbstractEmbeddableGremlinPropertyAccessor(AbstractGremlinFieldPropertyAccessor embeddedAccessor) {
        this.embeddedAccessor = embeddedAccessor;
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

    public abstract Object newInstance();

    /**
     * @return the root Field of this GremlinEmbeddedFieldAccessor
     */
    public Field getRootField() {
        AbstractEmbeddableGremlinPropertyAccessor rootFieldAccessor = this;
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

}
