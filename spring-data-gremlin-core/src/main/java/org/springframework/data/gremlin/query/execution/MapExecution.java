package org.springframework.data.gremlin.query.execution;

import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Executes the query to return a Map of properties.
 *
 * @author Gman
 */
@SuppressWarnings("unchecked")
public class MapExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public MapExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters) {
        super(schemaFactory, parameters);
    }

    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {

        Iterator<Vertex> result = ((Iterable<Vertex>) query.runQuery(parameters, values)).iterator();
        Vertex vertex = result.next();
        if (vertex == null) {
            return null;
        }
        if (result.hasNext()) {
            throw new IllegalArgumentException("The query resulted in multiple Vertices. Expected only one result for this Execution.");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : vertex.getPropertyKeys()) {
            map.put(key, vertex.getProperty(key));
        }
        return map;
    }
}
