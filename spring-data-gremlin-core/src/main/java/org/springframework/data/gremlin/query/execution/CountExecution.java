package org.springframework.data.gremlin.query.execution;

import com.tinkerpop.blueprints.Vertex;
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
        Iterator<Vertex> result = ((Iterable<Vertex>) query.runQuery(parameters, values, true)).iterator();
        long counter = 0L;

        try {
            while (true) {
                result.next();
                ++counter;
            }
        } catch (NoSuchElementException var4) {
            return counter;
        }
    }
}
