package org.springframework.data.gremlin.schema.property.accessor;

import java.lang.reflect.Field;

/**
 * A concrete {@link AbstractGremlinFieldPropertyAccessor} for basic Fields.
 *
 * @author Gman
 */
public class GremlinFieldPropertyAccessor<V> extends AbstractGremlinFieldPropertyAccessor<V> {

    private GremlinFieldPropertyAccessor parentAccessor;

    public GremlinFieldPropertyAccessor(Field field) {
        super(field);
    }

    public GremlinFieldPropertyAccessor(Field field, GremlinFieldPropertyAccessor parentAccessor) {
        super(field);
        this.parentAccessor = parentAccessor;
    }

    @Override
    public V get(Object object) {
        try {

            if (parentAccessor != null) {
                Object embeddedObj = parentAccessor.get(object);
                if (embeddedObj == null) {
                    return null;
                }
                object = embeddedObj;
            }
            V result = (V) field.get(object);
            return result;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, V val) {

        try {

            if (parentAccessor != null) {
                Object embeddedObj = parentAccessor.get(object);
                if (embeddedObj == null) {
                    embeddedObj = parentAccessor.newInstance();
                    parentAccessor.set(object, embeddedObj);
                }
                object = embeddedObj;
            }
            field.set(object, val);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public Object newInstance() {
        try {
            return field.getType().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the root Field of this GremlinEmbeddedFieldAccessor
     */
    public Field getRootField() {
        GremlinFieldPropertyAccessor rootFieldAccessor = this;
        GremlinFieldPropertyAccessor parentFieldAccessor = rootFieldAccessor.getParentAccessor();
        while (parentFieldAccessor != null) {
            rootFieldAccessor = parentFieldAccessor;
            parentFieldAccessor = rootFieldAccessor.getParentAccessor();
        }
        return rootFieldAccessor.getField();
    }

    public GremlinFieldPropertyAccessor getParentAccessor() {
        return parentAccessor;
    }

}
