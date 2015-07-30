package org.springframework.data.gremlin.query;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Gremlin specific extension of {@link QueryMethod} providing the {@link Method} and {@link Query} annotaiton.
 *
 * @author Gman
 */
public final class GremlinQueryMethod extends QueryMethod {

    /** The method. */
    private final Method method;

    /**
     * Instantiates a new {@link GremlinQueryMethod}.
     *
     * @param method   the method
     * @param metadata the metadata
     */
    public GremlinQueryMethod(Method method, RepositoryMetadata metadata) {
        super(method, metadata);
        this.method = method;
    }

    /**
     * Gets the target method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Returns whether the method has an annotated query.
     *
     * @return
     */
    public boolean hasAnnotatedQuery() {
        return getAnnotatedQuery() != null;
    }

    /**
     * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation found
     * nor the attribute was specified.
     *
     * @return the query
     */
    String getAnnotatedQuery() {
        String query = (String) AnnotationUtils.getValue(getQueryAnnotation());
        return StringUtils.hasText(query) ? query : null;
    }

    /**
     * Returns the {@link Query} annotation that is applied to the method or {@code null} if none available.
     *
     * @return
     */
    Query getQueryAnnotation() {
        return method.getAnnotation(Query.class);
    }

}
