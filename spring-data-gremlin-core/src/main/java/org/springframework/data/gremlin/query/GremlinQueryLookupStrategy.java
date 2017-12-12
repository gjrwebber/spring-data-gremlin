package org.springframework.data.gremlin.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Constructor;

/**
 * Implementation of {@link QueryLookupStrategy} for Gremlin.
 *
 * @author Gman
 */
public final class GremlinQueryLookupStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinQueryLookupStrategy.class);

    private GremlinQueryLookupStrategy() {
        super();
    }

    private abstract static class AbstractQueryLookupStrategy implements QueryLookupStrategy {

        protected GremlinGraphFactory dbf;
        protected GremlinSchemaFactory schemaFactory;
        protected GremlinGraphAdapter graphAdapter;

        private AbstractQueryLookupStrategy(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter) {
            super();
            this.schemaFactory = schemaFactory;
            this.dbf = dbf;
            this.graphAdapter = graphAdapter;
        }

        public final RepositoryQuery resolveQuery(java.lang.reflect.Method method, RepositoryMetadata metadata, ProjectionFactory factory,  NamedQueries namedQueries) {
            return resolveQuery(new GremlinQueryMethod(method, metadata, factory), namedQueries);
        }

        protected abstract RepositoryQuery resolveQuery(GremlinQueryMethod method, NamedQueries namedQueries);
    }

    private static class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

        private CreateQueryLookupStrategy(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter) {super(dbf, schemaFactory, graphAdapter);}

        @Override
        protected RepositoryQuery resolveQuery(GremlinQueryMethod method, NamedQueries namedQueries) {
            try {
                return new PartTreeGremlinQuery(dbf, schemaFactory, graphAdapter, method);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Could not create query metamodel for method %s!", method.toString()), e);
            }
        }
    }

    private static class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {

        protected Class<? extends AbstractNativeGremlinQuery> nativeQueryType;

        private DeclaredQueryLookupStrategy(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter,
                                            Class<? extends AbstractNativeGremlinQuery> nativeQueryType) {
            super(dbf, schemaFactory, graphAdapter);
            this.nativeQueryType = nativeQueryType;
        }

        @Override
        protected RepositoryQuery resolveQuery(GremlinQueryMethod method, NamedQueries namedQueries) {
            String query = method.getAnnotatedQuery();
            RepositoryQuery repoQuery = null;
            if (query != null) {
                if (method.getQueryAnnotation().nativeQuery()) {
                    if (nativeQueryType == null) {
                        throw new IllegalStateException(
                                "Native query wanted, but no implemented NativeGremlinQuery provided. You need to provide the type when setting up the GremlinRepositoryContext.");
                    }
                    try {
                        Constructor<?> constructor = nativeQueryType.getConstructor(GremlinGraphFactory.class, GremlinQueryMethod.class, GremlinSchemaFactory.class, GremlinGraphAdapter.class,
                                                                                    String.class);
                        repoQuery = (RepositoryQuery) constructor.newInstance(dbf, method, schemaFactory, graphAdapter, query);
                    } catch (Exception e) {
                        throw new IllegalStateException(String.format("Could not create a %s! Error: %s", nativeQueryType, e.getMessage()), e);
                    }
                } else {
                    repoQuery = new StringBasedGremlinQuery(dbf, schemaFactory, graphAdapter, query, method);
                }
            }
            return repoQuery;
        }
    }

    private static class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

        /** The declared query strategy. */
        private final DeclaredQueryLookupStrategy strategy;

        /** The create query strategy. */
        private final CreateQueryLookupStrategy createStrategy;

        /**
         * Instantiates a new {@link CreateIfNotFoundQueryLookupStrategy} lookup strategy.
         *
         * @param dbf
         */
        public CreateIfNotFoundQueryLookupStrategy(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter,
                                                   Class<? extends AbstractNativeGremlinQuery> nativeQueryType) {
            super(dbf, schemaFactory, graphAdapter);
            this.strategy = new DeclaredQueryLookupStrategy(dbf, schemaFactory, graphAdapter, nativeQueryType);
            this.createStrategy = new CreateQueryLookupStrategy(dbf, schemaFactory, graphAdapter);
        }

        @Override
        protected RepositoryQuery resolveQuery(GremlinQueryMethod method, NamedQueries namedQueries) {
            RepositoryQuery repoQuery = null;
            try {
                repoQuery = strategy.resolveQuery(method, namedQueries);
            } catch (IllegalStateException e) {
                LOGGER.warn(e.getMessage(), e);
            }

            if (repoQuery == null) {
                repoQuery = createStrategy.resolveQuery(method, namedQueries);
            }
            return repoQuery;
        }
    }

    public static QueryLookupStrategy create(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter, Class<? extends AbstractNativeGremlinQuery> nativeQueryType,
                                             Key key) {
        if (key == null) {
            return new CreateIfNotFoundQueryLookupStrategy(dbf, schemaFactory, graphAdapter, nativeQueryType);
        }

        switch (key) {
        case CREATE:
            return new CreateQueryLookupStrategy(dbf, schemaFactory, graphAdapter);
        case USE_DECLARED_QUERY:
            return new DeclaredQueryLookupStrategy(dbf, schemaFactory, graphAdapter, nativeQueryType);
        case CREATE_IF_NOT_FOUND:
            return new CreateIfNotFoundQueryLookupStrategy(dbf, schemaFactory, graphAdapter, nativeQueryType);
        default:
            throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
        }
    }

}
