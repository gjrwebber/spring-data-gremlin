package org.springframework.data.gremlin.schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A factory for {@link GremlinSchema}s mapped by Class type.
 *
 * @author Gman
 */
@Component
public class GremlinSchemaFactory {

    @Autowired
    private Set<GremlinSchema> schemas;

    private Map<Class<?>, GremlinSchema<?>> schemaMap = new HashMap<Class<?>, GremlinSchema<?>>();

    @PostConstruct
    public void init() {
        for (GremlinSchema schema : schemas) {
            schemaMap.put(schema.getClassType(), schema);
        }
    }

    public <V> GremlinSchema<V> getSchema(Class<V> type) {
        return (GremlinSchema<V>) schemaMap.get(type);
    }

}
