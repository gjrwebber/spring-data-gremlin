package org.springframework.data.gremlin.tx;

import com.tinkerpop.blueprints.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

/**
 * An abstract factory for creating {@link Graph} objects for concrete implementations.
 *
 * @author Gman
 */
public abstract class AbstractGremlinGraphFactory<T extends Graph> implements GremlinGraphFactory<T> {

    /** The logger. */
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractGremlinGraphFactory.class);

    /** The username. */
    protected String username = DEFAULT_USERNAME;

    /** The password. */
    protected String password = DEFAULT_PASSWORD;

    /** The min pool size. */
    protected int minPoolSize = DEFAULT_MIN_POOL_SIZE;

    /** The max pool size. */
    protected int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    protected String url;

    protected boolean autoCreate = true;

    @PostConstruct
    public void init() {

        createPool();
        if (autoCreate) {
            createGraph();
        }
    }

    /**
     * Create the Graph if it doesn't already exist
     */
    protected abstract void createGraph();

    /**
     * Creates a Graph Pool
     */
    protected abstract void createPool();

    /**
     * Open the Graph.
     *
     * @return the &gt;T&lt; Graph
     */
    public abstract T openGraph();

    @Override
    public void shutdown(T graph) {
        graph.shutdown();
    }

    @Override
    public T graph() {
        T graph = (T) TransactionSynchronizationManager.getResource(this);
        if (graph == null) {
            graph = openGraph();
            LOGGER.debug("acquire graph from pool {}", graph.hashCode());
        } else {

            if (isClosed(graph)) {
                graph = openGraph();
                LOGGER.debug("re-opened graph {}", graph.hashCode());
            } else {
                LOGGER.debug("use existing graph {}", graph.hashCode());
            }
        }

        return graph;
    }

    /**
     * Gets the database url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the database url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the min pool size.
     *
     * @return the min pool size
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * Sets the min pool size.
     *
     * @param minPoolSize the new min pool size
     */
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    /**
     * Gets the max pool size.
     *
     * @return the max pool size
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the max pool size.
     *
     * @param maxPoolSize the new max pool size
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean getAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
}
