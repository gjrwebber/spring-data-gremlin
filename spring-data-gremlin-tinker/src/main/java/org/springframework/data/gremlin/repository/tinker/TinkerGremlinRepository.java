package org.springframework.data.gremlin.repository.tinker;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
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
import java.util.function.Consumer;

/**
 * Tinker specific extension of the {@link SimpleGremlinRepository} providing custom implementations of {@code count()}, {@code deleteAll()},
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
            for (Element el : findAllElementsForSchema()) {
                count++;
            }
        } catch (Exception e) {
        }
        return count;
    }

    @Transactional
    @Override
    public void deleteAll() {
        for (Element element : findAllElementsForSchema()) {
            element.remove();
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        List<T> result = new ArrayList<T>();
        int total = 0;
        int prevOffset = pageable.getOffset();
        int offset = pageable.getOffset() + pageable.getPageSize();
        for (Element element : findAllElementsForSchema()) {
            if (total >= prevOffset && total < offset) {
                result.add(schema.loadFromGraph(graphAdapter, element));
            }
            total++;
        }
        return new PageImpl<T>(result, pageable, total);
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<T>();
        for (Element edge : findAllElementsForSchema()) {
            result.add(schema.loadFromGraph(graphAdapter, edge));
        }
        return result;
    }

    public Iterable<Element> findAllElementsForSchema() {

        if (schema.isVertexSchema()) {
            return findAllVerticiesForSchema();
        } else if (schema.isEdgeSchema()) {
            return findAllEdgesForSchema();
        } else {
            throw new IllegalStateException("GremlinSchema is neither VERTEX or EDGE!!");
        }
    }

    public Iterable<Element> findAllVerticiesForSchema() {
        final List<Element> result = new ArrayList<>();
        graphFactory.graph().traversal().V().hasLabel(schema.getClassName()).forEachRemaining(new Consumer<Vertex>() {
            @Override
            public void accept(Vertex vertex) {

                result.add(vertex);
            }
        });
        return result;
    }

    public Iterable<Element> findAllEdgesForSchema() {
        final List<Element> result = new ArrayList<>();
        graphFactory.graph().traversal().E().hasLabel(schema.getClassName()).forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                result.add(edge);
            }
        });
        return result;
    }

}
