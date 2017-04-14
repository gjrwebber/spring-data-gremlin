package org.springframework.data.gremlin.support;

import org.springframework.data.gremlin.query.AbstractNativeGremlinQuery;
import org.springframework.data.gremlin.query.GremlinQueryLookupStrategy;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.property.accessor.GremlinIdFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinIdPropertyAccessor;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

import java.io.Serializable;
import java.lang.reflect.Constructor;


/**
 * A {@link RepositoryFactorySupport} for Gremlin.
 *
 * @author Gman
 */
public class GremlinRepositoryFactory extends RepositoryFactorySupport {

    protected final GremlinGraphFactory dbf;
    protected final GremlinGraphAdapter graphAdapter;
    protected final GremlinSchemaFactory schemaFactory;
    protected final SchemaWriter schemaWriter;
    protected final Class<? extends AbstractNativeGremlinQuery> nativeQueryType;
    protected final Class<? extends GremlinRepository> repositoryType;

    public GremlinRepositoryFactory(GremlinRepositoryContext context) {
        this.dbf = context.getGraphFactory();
        this.graphAdapter = context.getGraphAdapter();
        this.schemaFactory = context.getSchemaFactory();
        this.schemaWriter = context.getSchemaWriter();
        this.nativeQueryType = context.getNativeQueryType();
        this.repositoryType = context.getRepositoryType();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getEntityInformation(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        GremlinSchema schema = schemaFactory.getSchema(domainClass);
        GremlinIdPropertyAccessor idAccessor = schema.getIdAccessor();
        return (EntityInformation<T, ID>) new GremlinMetamodelEntityInformation<T>(domainClass, idAccessor);
    }


    /* (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object getTargetRepository(RepositoryInformation metadata) {
        EntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
        Class<?> javaType = entityInformation.getJavaType();

        try {
            Constructor<?> constructor = repositoryType.getConstructor(GremlinGraphFactory.class, GremlinGraphAdapter.class, GremlinSchema.class);

            GremlinSchema schema = schemaFactory.getSchema(javaType);
            Object repository = constructor.newInstance(dbf, graphAdapter, schema);
            schema.setRepository((GremlinRepository) repository);
            schema.setGraphFactory(dbf);

            if (schemaWriter != null) {
                schemaWriter.writeSchema(dbf, schema);
            }

            return repository;
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Could not create a %s! Error: %s", repositoryType, e.getMessage()), e);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getRepositoryBaseClass(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return repositoryType;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getQueryLookupStrategy(org.springframework.data.repository.query.QueryLookupStrategy.Key)
     */
    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(Key key, EvaluationContextProvider provider) {
        return GremlinQueryLookupStrategy.create(dbf, schemaFactory, graphAdapter, nativeQueryType, key);
    }

}
