package org.springframework.data.gremlin.query;

import java.util.Map;

/**
 * A query result object containing the mapped entity along with any external properties requested in the query.
 *
 * @author Gman
 */
public class CompositeResult<V> {

    private V entity;
    private Map<String, Object> properties;

    public CompositeResult(V entity, Map<String, Object> properties) {
        this.entity = entity;
        this.properties = properties;
    }

    public V getEntity() {
        return entity;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
