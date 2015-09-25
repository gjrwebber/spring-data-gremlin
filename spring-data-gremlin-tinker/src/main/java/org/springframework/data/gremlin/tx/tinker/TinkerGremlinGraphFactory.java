package org.springframework.data.gremlin.tx.tinker;


import org.apache.tinkerpop.gremlin.structure.impls.tg.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.tx.AbstractGremlinGraphFactory;

/**
 * An {@link AbstractGremlinGraphFactory} for OrentDB providing an {@link TinkerGraph} implementation of {@link org.apache.tinkerpop.gremlin.structure.Graph}.
 *
 * @author Gman
 */
public class TinkerGremlinGraphFactory extends AbstractGremlinGraphFactory<TinkerGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerGremlinGraphFactory.class);

    private TinkerGraph graph = null;

    @Override
    protected void createPool() {
        graph = new TinkerGraph();
    }

    @Override
    public boolean isActive(TinkerGraph graph) {
        return false;
    }

    @Override
    public boolean isClosed(TinkerGraph graph) {
        return false;
    }

    @Override
    public void beginTx(TinkerGraph graph) {
    }

    @Override
    public void commitTx(TinkerGraph graph) {
    }

    @Override
    public void rollbackTx(TinkerGraph graph) {
    }

    @Override
    public TinkerGraph openGraph() {
        return graph;
    }

    @Override
    protected void createGraph() {
        LOGGER.warn("Cannot create database on remote connections.");
    }
}
