package org.springframework.data.gremlin.repository;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of the {@link org.springframework.data.repository.PagingAndSortingRepository} interface for Gremlin.
 *
 * @param <T> the type of the entity to handle
 * @author Gman
 */
@Repository
@Transactional(readOnly = true)
public class SimpleGremlinRepository<T> implements GremlinRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGremlinRepository.class);

    protected GremlinGraphFactory dbf;

    protected GremlinSchema<T> schema;

    protected GremlinGraphAdapter graphAdapter;

    public SimpleGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> schema) {
        this.dbf = dbf;
        this.graphAdapter = graphAdapter;
        this.schema = schema;
    }

    @Transactional(readOnly = false)
    public Vertex create(Graph graph, final T object) {
        final Vertex vertex = graphAdapter.createVertex(graph, schema.getClassName());
        schema.copyToGraph(graphAdapter, vertex, object);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    schema.setObjectId(object, vertex);
                }
            });
        }
        return vertex;
    }

    @Transactional(readOnly = false)
    public T save(Graph graph, T object) {

        String id = schema.getObjectId(object);
        if (StringUtils.isEmpty(id)) {
            create(graph, object);
        } else {
            Vertex vertex = graphAdapter.getVertex(schema.decodeId(id));
            if (vertex == null) {
                throw new IllegalStateException(String.format("Could not save %s with id %s, as it does not exist.", object, id));
            }
            schema.copyToGraph(graphAdapter, vertex, object);
        }
        return object;
    }

    @Transactional(readOnly = false)
    @Override
    public <S extends T> S save(S s) {
        Graph graph = dbf.graph();

        String id = schema.getObjectId(s);
        if (!StringUtils.isEmpty(id)) {
            save(graph, s);
        } else {
            create(graph, s);
        }
        return s;

    }

    @Transactional(readOnly = false)
    @Override
    public <S extends T> Iterable<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            save(s);
        }
        return iterable;
    }

    @Override
    public T findOne(String id) {
        T object = null;
        Vertex vertex = graphAdapter.findVertexById(id);

        if (vertex != null) {
            object = schema.loadFromGraph(vertex);
        }

        return object;
    }

    @Override
    public boolean exists(String id) {
        return count() == 1;
    }

    @Override
    public Iterable<T> findAll() {
        throw new NotImplementedException("Finding all vertices in Graph databases does not really make sense. So, it hasn't been implemented.");
    }

    @Override
    public Iterable<T> findAll(Iterable<String> iterable) {
        Set<T> objects = new HashSet<T>();
        for (String id : iterable) {
            objects.add(findOne(id));
        }
        return objects;
    }

    @Override
    public long count() {
        throw new NotImplementedException("Counting all vertices in Gremlin has not been implemented.");
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(String id) {
        Vertex v = graphAdapter.findVertexById(id);
        graphAdapter.removeVertex(v);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(T t) {
        delete(schema.getObjectId(t));
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Iterable<? extends T> iterable) {
        for (T t : iterable) {
            delete(t);
        }
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException("Deleting all vertices in Gremlin has not been implemented.");
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        throw new NotImplementedException("Sorting all vertices in Gremlin has not been implemented.");
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new NotImplementedException("Deleting all vertices in Gremlin has not been implemented.");
    }


}
