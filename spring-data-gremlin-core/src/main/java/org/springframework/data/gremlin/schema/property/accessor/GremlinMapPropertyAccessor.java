package org.springframework.data.gremlin.schema.property.accessor;

import org.springframework.util.Assert;

import java.util.Map;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public class GremlinMapPropertyAccessor<V> extends AbstractEmbeddableGremlinPropertyAccessor<V> {

    private String propertyName;
    private Class<?> propertyType;

    public GremlinMapPropertyAccessor(String propertyName, Class<?> propertyType, GremlinMapPropertyAccessor embeddedAccessor) {
        super(embeddedAccessor);
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public Object newInstance() {
        try {
            return propertyType.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create a new instance of " + propertyType + ": " + e.getMessage(), e);
        }
    }

    @Override
    public V get(Object object) {

        Assert.isTrue(object instanceof Map);

        Map<String, Object> map = (Map<String, Object>) object;

        object = getEmbeddedObject(object, false);
        V result = null;
        if (object != null) {
            result = (V) map.get(object);
        }
        return result;
    }

    @Override
    public void set(Object object, V val) {

        Assert.isTrue(object instanceof Map);
        Map<String, Object> map = (Map<String, Object>) object;

        object = getEmbeddedObject(object, true);
        if (object != null) {
            map.put(propertyName, val);
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinMapPropertyAccessor{");
        sb.append("propertyName='").append(propertyName).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append('}');
        return sb.toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }
}
