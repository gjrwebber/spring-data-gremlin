package org.springframework.data.gremlin.query.execution;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.*;

/**
 * Executes the query to return a single entity.
 *
 * @author Gman
 */
@SuppressWarnings("unchecked")
public class SingleEntityExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public SingleEntityExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        Class<?> mappedType = query.getQueryMethod().getReturnedObjectType();

        List<Vertex> vertices = ((GraphTraversal)query.runQuery(parameters, values)).toList();
        Vertex vertex;
        if (vertices.size() > 1) {
            throw new IllegalArgumentException("The query resulted in multiple Vertices. Expected only one result for this Execution.");
        }
        else if(vertices.size() == 1) {
            vertex = vertices.get(0);
        }
        else {
            return null;
        }

        if (mappedType.isAssignableFrom(Map.class)) {

            Map<String, Object> map = elementToMap(vertex);
            return map;
        } else {
            GremlinSchema mapper = schemaFactory.getSchema(mappedType);
            return mapper.loadFromGraph(graphAdapter, vertex);

        }
    }
}
