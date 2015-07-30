package org.springframework.data.gremlin.repository;

import org.springframework.data.gremlin.query.AbstractNativeGremlinQuery;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

/**
 * An encapsulation of GremlinRepository data.
 *
 * @author Gman
 */
public class GremlinRepositoryContext {

    private GremlinGraphFactory graphFactory;
    private GremlinGraphAdapter graphAdapter;
    private GremlinSchemaFactory schemaFactory;
    private SchemaWriter schemaWriter;
    private Class<? extends AbstractNativeGremlinQuery> nativeQueryType;
    private Class<? extends GremlinRepository> repositoryType;

    public GremlinRepositoryContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory) {
        this(graphFactory, graphAdapter, schemaFactory, null);
    }

    public GremlinRepositoryContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter) {
        this(graphFactory, graphAdapter, schemaFactory, schemaWriter, SimpleGremlinRepository.class, null);
    }

    public GremlinRepositoryContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter,
                                    Class<? extends GremlinRepository> repositoryType) {
        this(graphFactory, graphAdapter, schemaFactory, schemaWriter, repositoryType, null);
    }

    public GremlinRepositoryContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter,
                                    Class<? extends GremlinRepository> repositoryType, Class<? extends AbstractNativeGremlinQuery> nativeQueryType) {
        this.graphFactory = graphFactory;
        this.graphAdapter = graphAdapter;
        this.schemaFactory = schemaFactory;
        this.schemaWriter = schemaWriter;
        this.nativeQueryType = nativeQueryType;
        this.repositoryType = repositoryType;
    }

    public GremlinGraphFactory getGraphFactory() {
        return graphFactory;
    }

    public GremlinGraphAdapter getGraphAdapter() {
        return graphAdapter;
    }

    public GremlinSchemaFactory getSchemaFactory() {
        return schemaFactory;
    }

    public SchemaWriter getSchemaWriter() {
        return schemaWriter;
    }


    public Class<? extends GremlinRepository> getRepositoryType() {
        return repositoryType;
    }

    public Class<? extends AbstractNativeGremlinQuery> getNativeQueryType() {
        return nativeQueryType;
    }
}
