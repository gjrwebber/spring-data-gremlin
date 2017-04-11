package org.springframework.data.gremlin.query.execution;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Executes the query to return the sum of entities.
 *
 * @author Gman
 */
@SuppressWarnings("unchecked")
public class CountExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link org.springframework.data.gremlin.query.execution.CountExecution}.
     */
    public CountExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    /* (non-Javadoc)
         * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
         */
    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        return ((GraphTraversal) query.runQuery(parameters, values, true)).count().next();
    }
}
