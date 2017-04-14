package org.springframework.data.gremlin.query.execution;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    public MapExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {

        List<Vertex> result = ((GraphTraversal) query.runQuery(parameters, values)).toList();
        if (result.size() > 1) {
            throw new IllegalArgumentException("The query resulted in multiple Vertices. Expected only one result for this Execution.");
        }
        else if (result.size() == 1) {
            Vertex vertex = result.get(0);
            if (vertex == null) {
                return null;
            }
            Map<String, Object> map = elementToMap(vertex);
            return map;
        }
        else {
            return null;
        }
    }

}
