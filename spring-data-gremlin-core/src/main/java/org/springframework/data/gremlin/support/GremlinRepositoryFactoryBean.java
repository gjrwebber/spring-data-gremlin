package org.springframework.data.gremlin.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.repository.GremlinRepository;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @param <T> the type of the repository
 * @param <S> the type of the entity
 * @author Gman
 */
public class GremlinRepositoryFactoryBean<T extends GremlinRepository<S>, S> extends TransactionalRepositoryFactoryBeanSupport<T, S, String> {

    /** The orient operations. */
    @Autowired
    private GremlinRepositoryContext context;

    public GremlinRepositoryFactoryBean() {
    }

    /* (non-Javadoc)
         * @see org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport#doCreateRepositoryFactory()
         */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new GremlinRepositoryFactory(context);
    }
}
