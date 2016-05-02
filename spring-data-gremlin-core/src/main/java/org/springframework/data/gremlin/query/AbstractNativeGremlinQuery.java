package org.springframework.data.gremlin.query;

import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

/**
 * Native query abstraction for {@link AbstractGremlinQuery}s.
 *
 * @author Gman
 */
public abstract class AbstractNativeGremlinQuery extends AbstractGremlinQuery {

    /** The query method. */
    protected final GremlinGraphFactory dbf;
    protected final GremlinQueryMethod method;
    protected final GremlinSchemaFactory schemaFactory;
    protected final GremlinGraphAdapter graphAdapter;
    protected final String query;
    private boolean countQuery;
    private boolean modifyingQuery;

    public AbstractNativeGremlinQuery(GremlinGraphFactory dbf, GremlinQueryMethod method, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter, String query) {
        super(schemaFactory, method, graphAdapter);

        this.dbf = dbf;
        this.method = method;
        this.schemaFactory = schemaFactory;
        this.graphAdapter = graphAdapter;
        this.query = query;
        this.countQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().count();
        this.modifyingQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().modify();
    }

    public GremlinGraphFactory getDbf() {
        return dbf;
    }

    public GremlinQueryMethod getMethod() {
        return method;
    }

    public GremlinSchemaFactory getSchemaFactory() {
        return schemaFactory;
    }

    public GremlinGraphAdapter getGraphAdapter() {
        return graphAdapter;
    }

    public String getQuery() {
        return query;
    }

    @Override
    protected boolean isCountQuery() {
        return countQuery;
    }

    @Override
    protected boolean isModifyingQuery() {
        return modifyingQuery;
    }
}
