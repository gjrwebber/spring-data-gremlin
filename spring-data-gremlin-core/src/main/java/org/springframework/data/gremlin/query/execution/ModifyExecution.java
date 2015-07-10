package org.springframework.data.gremlin.query.execution;

import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

/**
 * Executes the query to return a sum of entities.
 *
 * @author Gman
 */
public class ModifyExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public ModifyExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters) {
        super(schemaFactory, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        return query.runQuery(parameters, values, true);
    }
}
