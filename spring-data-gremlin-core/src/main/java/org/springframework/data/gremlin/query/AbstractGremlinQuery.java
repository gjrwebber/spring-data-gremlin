package org.springframework.data.gremlin.query;

import org.springframework.data.gremlin.query.execution.*;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.RepositoryQuery;

import java.util.Map;

/**
 * The base class to implement {@link RepositoryQuery}s for Gremlin.
 *
 * @author Gman
 */
public abstract class AbstractGremlinQuery implements RepositoryQuery {

    /** The query method. */
    protected final GremlinQueryMethod method;
    protected final GremlinSchemaFactory schemaFactory;

    /**
     * Instantiates a new {@link AbstractGremlinQuery}.
     *
     * @param method the query method
     */
    public AbstractGremlinQuery(GremlinSchemaFactory schemaFactory, GremlinQueryMethod method) {
        super();
        this.schemaFactory = schemaFactory;
        this.method = method;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.RepositoryQuery#getQueryMethod()
     */
    public GremlinQueryMethod getQueryMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.RepositoryQuery#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object[] parameters) {
        return doExecute(getExecution(), parameters);
    }

    /**
     * Do execute.
     *
     * @param execution the execution
     * @param values    the values
     * @return the object
     */
    protected Object doExecute(AbstractGremlinExecution execution, Object[] values) {
        return execution.execute(this, values);
    }

    /**
     * Creates the orient query.
     *
     * @param parameters
     * @param values     the parameters for query
     * @return the OSQL query
     */
    @SuppressWarnings("rawtypes")
    public Object runQuery(DefaultParameters parameters, Object[] values) {
        return runQuery(parameters, values, false);
    }

    /**
     * Creates the orient query.
     *
     * @param parameters
     * @param values     the parameters for query
     * @return the OSQL query
     */
    @SuppressWarnings("rawtypes")
    public Object runQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {
        return doRunQuery(parameters, values, ignorePaging);
    }

    /**
     * Do create query for specific source.
     *
     * @param parameters
     * @param values       the parameters for query
     * @param ignorePaging
     * @return the OSQL query
     */
    @SuppressWarnings("rawtypes")
    protected abstract Object doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging);

    /**
     * Gets the execution for query.
     *
     * @return the execution
     */
    protected AbstractGremlinExecution getExecution() {
        final DefaultParameters parameters = (DefaultParameters) method.getParameters();

        if (method.isCollectionQuery()) {
            return new CollectionExecution(schemaFactory, parameters);
        } else if (method.isPageQuery()) {
            return new CollectionExecution(schemaFactory, parameters);
        } else if (method.isQueryForEntity()) {
            return new SingleEntityExecution(schemaFactory, parameters);
        } else if (isModifyingQuery()) {
            return new ModifyExecution(schemaFactory, parameters);
        } else if (isCountQuery()) {
            return new CountExecution(schemaFactory, parameters);
        } else if (isMapQuery()) {
            return new MapExecution(schemaFactory, parameters);
        } else if (isCompositeQuery()) {
            return new CompositeExecution(schemaFactory, parameters);
        }

        throw new IllegalArgumentException();
    }

    /**
     * @return true if this query is a count query
     */
    protected abstract boolean isCountQuery();

    /**
     * @return true if this query is a modifying query
     */
    protected abstract boolean isModifyingQuery();

    private boolean isMapQuery() {
        return method.getReturnedObjectType().isAssignableFrom(Map.class);
    }

    private boolean isCompositeQuery() {
        return method.getReturnedObjectType() == CompositeResult.class;
    }


}
