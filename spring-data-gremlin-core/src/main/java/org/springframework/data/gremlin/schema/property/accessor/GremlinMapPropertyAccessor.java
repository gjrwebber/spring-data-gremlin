package org.springframework.data.gremlin.schema.property.accessor;

import org.springframework.util.Assert;

import java.util.Map;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public class GremlinMapPropertyAccessor<V> implements GremlinPropertyAccessor<V> {

    private String propertyName;

    public GremlinMapPropertyAccessor(String propertyName) {
        Assert.hasLength(propertyName);
        this.propertyName = propertyName;
    }

    @Override
    public V get(Object object) {

        Assert.isTrue(object instanceof Map);

        Map<String, Object> map = (Map<String, Object>) object;

        V result = null;
        if (map != null) {
            result = (V) map.get(propertyName);
        }
        return result;
    }

    @Override
    public void set(Object object, V val) {

        Assert.isTrue(object instanceof Map);
        Map<String, Object> map = (Map<String, Object>) object;

        if (map != null) {
            map.put(propertyName, val);
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinMapPropertyAccessor{");
        sb.append("propertyName='").append(propertyName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getPropertyName() {
        return propertyName;
    }

}
