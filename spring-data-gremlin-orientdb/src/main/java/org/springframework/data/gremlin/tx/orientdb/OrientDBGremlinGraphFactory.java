package org.springframework.data.gremlin.tx.orientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraph;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.tx.AbstractGremlinGraphFactory;

import static org.springframework.util.Assert.notNull;

/**
 * An {@link AbstractGremlinGraphFactory} for OrentDB providing an {@link OrientGraph} implementation of {@link com.tinkerpop.blueprints.Graph}.
 *
 * @author Gman
 */
public class OrientDBGremlinGraphFactory extends AbstractGremlinGraphFactory<OrientGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDBGremlinGraphFactory.class);

    private OrientGraphFactory ogf;

    public OrientGraph graphNoTx() {
        return ogf.getNoTx();
    }

    @Override
    protected void createPool() {

        notNull(url);
        notNull(username);
        notNull(password);

        ogf = new OrientGraphFactory(getUrl(), getUsername(), getPassword()).setupPool(getMinPoolSize(), getMaxPoolSize());
    }

    @Override
    public boolean isActive(OrientGraph graph) {
        return graph.tx().isOpen();
    }

    @Override
    public boolean isClosed(OrientGraph graph) {
        return graph.isClosed();
    }

    @Override
    public void beginTx(OrientGraph graph) {
        graph.begin();
    }

    @Override
    public void commitTx(OrientGraph graph) {
        graph.commit();
    }

    @Override
    public void rollbackTx(OrientGraph graph) {
        graph.rollback();
    }

    @Override
    public OrientGraph openGraph() {
        return ogf.getTx();
    }

    @Override
    protected void createGraph() {
        if (!getUrl().startsWith("remote:")) {
            ODatabase db = ogf.getTx().database();
            if (!db.exists()) {
                db.create();
                db.close();
            }
        } else {
            LOGGER.warn("Cannot create database on remote connections.");
        }
    }

}
