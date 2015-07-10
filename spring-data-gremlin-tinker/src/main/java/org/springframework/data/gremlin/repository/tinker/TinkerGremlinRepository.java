package org.springframework.data.gremlin.repository.tinker;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.SimpleGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.tinker.TinkerGremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Titan specific extension of the {@link SimpleGremlinRepository} providing custom implementations of {@code count()}, {@code deleteAll()},
 * {@code findAll(Pageable)} and {@code findAll()}.
 *
 * @author Gman
 */
public class TinkerGremlinRepository<T> extends SimpleGremlinRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerGremlinRepository.class);

    private TinkerGremlinGraphFactory graphFactory;

    public TinkerGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> mapper) {
        super(dbf, graphAdapter, mapper);
        this.graphFactory = (TinkerGremlinGraphFactory) dbf;
    }

    @Override
    @Transactional
    public long count() {
        long count = 0;
        try {
            for (Vertex v : getVertices()) {
                count++;
            }
        } catch (Exception e) {
        }
        return count;
    }

    @Transactional
    @Override
    public void deleteAll() {
        for (Vertex vertex : getVertices()) {
            vertex.remove();
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        List<T> result = new ArrayList<T>();
        int count = 0;
        int prevOffset = pageable.previousOrFirst().getOffset();
        int offset = pageable.getOffset();
        for (Vertex vertex : getVertices()) {
            if (count >= prevOffset && count < offset) {
                result.add(schema.loadFromVertex(vertex));
                count++;
            }
        }
        return new PageImpl<T>(result, pageable, count);
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<T>();
        for (Vertex vertex : getVertices()) {
            result.add(schema.loadFromVertex(vertex));
        }
        return result;
    }

    private Iterable<Vertex> getVertices() {
        return graphFactory.graph().getVertices("label", schema.getClassName());
    }
}
