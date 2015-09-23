package org.springframework.data.gremlin.tx.titan;

import static org.springframework.util.Assert.notNull;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.tx.AbstractGremlinGraphFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

/**
 * An {@link AbstractGremlinGraphFactory} for OrentDB providing an {@link TitanGraph} implementation of {@link com.tinkerpop.blueprints.Graph}.
 *
 * @author Gman
 */
public class TitanGremlinGraphFactory extends AbstractGremlinGraphFactory<TitanGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanGremlinGraphFactory.class);

    private TitanGraph graph = null;
    private Configuration configuration;

    @Override
    protected void createPool() {
        if(configuration != null){
            graph = TitanFactory.open(configuration);
        } else {
            notNull(url);
            graph = TitanFactory.open(url);
        }
    }

    @Override
    public boolean isActive(TitanGraph graph) {
        return graph.getManagementSystem().isOpen();
    }

    @Override
    public boolean isClosed(TitanGraph graph) {
        return graph.isClosed();
    }

    @Override
    public void beginTx(TitanGraph graph) {
        graph.newTransaction();
    }

    @Override
    public void commitTx(TitanGraph graph) {
        graph.commit();
    }

    @Override
    public void rollbackTx(TitanGraph graph) {
        graph.rollback();
    }

    @Override
    public TitanGraph openGraph() {
        if (graph == null || graph.isClosed()) {
            createPool();
        }
        return graph;
    }

    @Override
    protected void createGraph() {
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
