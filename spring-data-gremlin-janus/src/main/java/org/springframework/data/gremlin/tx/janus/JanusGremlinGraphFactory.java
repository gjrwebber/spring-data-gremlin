package org.springframework.data.gremlin.tx.janus;

import org.apache.commons.configuration.Configuration;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.tx.AbstractGremlinGraphFactory;

import static org.springframework.util.Assert.notNull;

/**
 * An {@link AbstractGremlinGraphFactory} for OrentDB providing an {@link JanusGraph} implementation of {@link org.apache.tinkerpop.gremlin.structure.Graph}.
 *
 * @author Gman
 */
public class JanusGremlinGraphFactory extends AbstractGremlinGraphFactory<JanusGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JanusGremlinGraphFactory.class);

    private JanusGraph graph = null;
    private Configuration configuration;

    @Override
    protected void createPool() {
        if (configuration != null) {
            graph = JanusGraphFactory.open(configuration);
        } else {
            notNull(url);
            graph = JanusGraphFactory.open(url);

        }
    }

    @Override
    public boolean isActive(JanusGraph graph) {
        return graph.isOpen();
    }

    @Override
    public boolean isClosed(JanusGraph graph) {
        return graph.isClosed();
    }

    @Override
    public void beginTx(JanusGraph graph) {
        graph.newTransaction();
    }

    @Override
    public void commitTx(JanusGraph graph) {
        graph.tx().commit();
    }

    @Override
    public void rollbackTx(JanusGraph graph) {
        graph.tx().rollback();
    }

    @Override
    public JanusGraph openGraph() {
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
