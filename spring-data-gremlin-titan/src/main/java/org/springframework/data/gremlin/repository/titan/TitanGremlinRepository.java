package org.springframework.data.gremlin.repository.titan;

import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
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
import org.springframework.data.gremlin.tx.titan.TitanGremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Titan specific extension of the {@link SimpleGremlinRepository} providing custom implementations of {@code count()}, {@code deleteAll()},
 * {@code findAll(Pageable)} and {@code findAll()}.
 *
 * @author Gman
 */
public class TitanGremlinRepository<T> extends SimpleGremlinRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanGremlinRepository.class);

    TitanGremlinGraphFactory graphFactory;

    public TitanGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> mapper) {
        super(dbf, graphAdapter, mapper);
        this.graphFactory = (TitanGremlinGraphFactory) dbf;
    }

    @Transactional(readOnly = false)
    protected Vertex createVertex(Graph graph) {
        Vertex vertex = ((TitanGraph) graph).addVertex(schema.getClassName());
        return vertex;
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
                result.add(schema.loadFromGraph(element));
            }
            total++;
        }
        return new PageImpl<T>(result, pageable, total);
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<T>();
        for (Element vertex : findAllElementsForSchema()) {
            result.add(schema.loadFromGraph(vertex));
        }
        return result;
    }

    public Iterable<Element> findAllElementsForSchema() {

        if (schema.isVertexSchema()) {
            return findALlVerticiesForSchema();
        } else if (schema.isEdgeSchema()) {
            return findAllEdgesForSchema();
        } else {
            throw new IllegalStateException("GremlinSchema is neither VERTEX or EDGE!!");
        }
    }

    public Iterable<Element> findALlVerticiesForSchema() {
        List<Element> result = new ArrayList<>();
        for (Vertex vertex : graphFactory.graph().getVertexLabel(schema.getClassName()).query().vertices()) {
            result.add(vertex);
        }
        return result;
    }

    public Iterable<Element> findAllEdgesForSchema() {
        List<Element> result = new ArrayList<>();
        for (Edge edge : graphFactory.graph().getVertexLabel(schema.getClassName()).query().edges()) {
            result.add(edge);
        }
        return result;
    }

}
