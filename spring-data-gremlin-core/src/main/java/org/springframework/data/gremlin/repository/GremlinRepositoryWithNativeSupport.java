package org.springframework.data.gremlin.repository;

import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The Gremlin specific extension of {@link PagingAndSortingRepository}.
 * <i>Note: </i> This implementation expects an if of type String. To be fixed.
 *
 * @param <T> the generic type to handle
 * @author Gman
 */
@NoRepositoryBean
public interface GremlinRepositoryWithNativeSupport<T> extends GremlinRepository<T> {
}
