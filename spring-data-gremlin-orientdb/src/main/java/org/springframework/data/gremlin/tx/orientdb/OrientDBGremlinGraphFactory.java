package org.springframework.data.gremlin.tx.orientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
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

    public OrientGraphNoTx graphNoTx() {
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
        return graph.getRawGraph().getTransaction().isActive();
    }

    @Override
    public boolean isClosed(OrientGraph graph) {
        return graph.isClosed();
    }

    @Override
    public void beginTx(OrientGraph graph) {
        // No need to begin the transaction as it is auto started.
        //        graph.getRawGraph().getTransaction().begin();
    }

    @Override
    public void commitTx(OrientGraph graph) {
        graph.getRawGraph().getTransaction().commit();
    }

    @Override
    public void rollbackTx(OrientGraph graph) {
        if (graph.getRawGraph().getTransaction().amountOfNestedTxs() > 0) {
            graph.getRawGraph().getTransaction().rollback();
        }
    }

    @Override
    public OrientGraph openGraph() {
        return ogf.getTx();
    }

    @Override
    protected void createGraph() {
        if (!getUrl().startsWith("remote:")) {
            ODatabase db = ogf.getDatabase();
            if (!db.exists()) {
                db.create();
                db.close();
            }
        } else {
            LOGGER.warn("Cannot create database on remote connections.");
        }
    }
}
