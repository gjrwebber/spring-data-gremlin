package org.springframework.data.gremlin.tx;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * An interface defining a Gremlin {@link Graph} factory.
 *
 * @param <T> The implementing type extending {@link Graph}
 * @author Gman
 */
public interface GremlinGraphFactory<T extends Graph> {
    /** Default database username. */
    String DEFAULT_USERNAME = "admin";

    /** Default database password. */
    String DEFAULT_PASSWORD = "admin";

    /** Default minimum pool size. */
    int DEFAULT_MIN_POOL_SIZE = 1;

    /** Default maximum pool size. */
    int DEFAULT_MAX_POOL_SIZE = 20;

    boolean isActive(T graph);

    boolean isClosed(T graph);

    void beginTx(T graph);

    void commitTx(T graph);

    void rollbackTx(T graph);

    T graph();

    T openGraph();

    void shutdown(T graph);

    String getUrl();

    void setUrl(String url);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    int getMinPoolSize();

    void setMinPoolSize(int minPoolSize);

    int getMaxPoolSize();

    void setMaxPoolSize(int maxPoolSize);

    Class<? extends RuntimeException> getRetryException();

    RuntimeException getForceRetryException();

    void resumeTx(T oldGraph);
}
