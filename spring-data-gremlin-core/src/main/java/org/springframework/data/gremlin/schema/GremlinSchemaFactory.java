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

    private Map<String, GremlinSchema<?>> schemaMap = new HashMap<String, GremlinSchema<?>>();

    @PostConstruct
    public void init() {
        for (GremlinSchema schema : schemas) {
            String key = schema.getClassType().getSimpleName();
            if (schemaMap.containsKey(key)) {
                key = schema.getClassName();
            }
            schemaMap.put(key, schema);
        }
    }

    public <V> GremlinSchema<V> getSchema(Class<V> type) {
        return (GremlinSchema<V>) schemaMap.get(type.getSimpleName());
    }

}
