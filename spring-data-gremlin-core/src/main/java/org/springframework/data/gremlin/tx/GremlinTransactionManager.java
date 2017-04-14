package org.springframework.data.gremlin.tx;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager} implementation
 * for Gremlin.
 *
 * @author Gman
 */
public class GremlinTransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinTransactionManager.class);
    private static final int RETRY_DELAY = Integer.getInteger("sdg-retry-delay", 50);
    private static final int MAX_RETRY = Integer.getInteger("sdg-max-retry", 10);

    private GremlinGraphFactory graphFactory;

    /**
     * Instantiates a new GremlinTransactionManager with the given GremlinGraphFactory.
     *
     * @param graphFactory the GremlinGraphFactory
     */
    public GremlinTransactionManager(GremlinGraphFactory graphFactory) {
        super();
        this.graphFactory = graphFactory;
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doGetTransaction()
     */
    @Override
    protected Object doGetTransaction() throws TransactionException {


        Graph db = (Graph) TransactionSynchronizationManager.getResource(getResourceFactory());

        GremlinTransaction tx = new GremlinTransaction(db);
        return tx;
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#isExistingTransaction(java.lang.Object)
     */
    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) transaction;

        boolean existing = tx.getGraph() != null && graphFactory.isActive(tx.getGraph());
        return existing;
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doBegin(java.lang.Object, org.springframework.transaction.TransactionDefinition)
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) transaction;

        if (tx.getGraph() == null || graphFactory.isClosed(tx.getGraph())) {
            tx.setGraph(graphFactory.graph());
            TransactionSynchronizationManager.bindResource(graphFactory, tx.getGraph());
        }

        LOGGER.debug("beginning transaction, db.hashCode() = {}", tx.getGraph().hashCode());

        graphFactory.beginTx(tx.getGraph());
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) status.getTransaction();
        Graph graph = tx.getGraph();
        int attempts = 0;
        while (attempts++ < MAX_RETRY) {
            try {
                graphFactory.commitTx(graph);
                break;
           } catch (RuntimeException e) {
               if (graphFactory.getRetryException().isAssignableFrom(e.getClass())) {
                   LOGGER.warn("Attempted to commit Tx " + attempts + " out of " + MAX_RETRY + " times. Waiting " + RETRY_DELAY + "ms before trying again. Error: "+e.getMessage());
                } else {
                    throw e;
                }
            }

            try {
                Thread.sleep(RETRY_DELAY);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doRollback(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) status.getTransaction();
        Graph graph = tx.getGraph();

        LOGGER.debug("rolling back transaction, db.hashCode() = {}", graph.hashCode());

        graphFactory.rollbackTx(graph);
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doSetRollbackOnly(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        status.setRollbackOnly();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCleanupAfterCompletion(java.lang.Object)
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        GremlinTransaction tx = (GremlinTransaction) transaction;

        if (tx.getGraph() == null || graphFactory.isClosed(tx.getGraph())) {
            LOGGER.debug("closing transaction, db.hashCode() = {}", tx.getGraph().hashCode());
            graphFactory.shutdown(tx.getGraph());
        }

        TransactionSynchronizationManager.unbindResource(graphFactory);
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doSuspend(java.lang.Object)
     */
    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) transaction;

        return tx.getGraph();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doResume(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        GremlinTransaction tx = (GremlinTransaction) transaction;

        if (!graphFactory.isClosed(tx.getGraph())) {
            graphFactory.shutdown(tx.getGraph());
        }

        Graph oldGraph = (Graph) suspendedResources;
        TransactionSynchronizationManager.bindResource(graphFactory, oldGraph);
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.ResourceTransactionManager#getResourceFactory()
     */
    @Override
    public Object getResourceFactory() {
        return graphFactory;
    }

    public GremlinGraphFactory getGraphFactory() {
        return graphFactory;
    }

    public void setGraphFactory(GremlinGraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }
}
