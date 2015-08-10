package org.springframework.data.gremlin.repository.orientdb;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.SimpleGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.orientdb.OrientDBGremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * OrientDB specific extension of the {@link SimpleGremlinRepository} providing custom implementations of {@code count()}, {@code deleteAll()},
 * {@code findAll(Pageable)} and {@code findAll()}.
 *
 * @author Gman
 */
public class OrientDBGremlinRepository<T> extends SimpleGremlinRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDBGremlinRepository.class);

    OrientDBGremlinGraphFactory orientGraphFactory;

    public OrientDBGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> mapper) {
        super(dbf, graphAdapter, mapper);
        this.orientGraphFactory = (OrientDBGremlinGraphFactory) dbf;
    }

    @Override
    @Transactional
    public long count() {
        long count = 0;
        try {
            count = orientGraphFactory.graph().countVertices(schema.getClassName());
        } catch (Exception e) {
        }
        return count;
    }

    @Transactional
    @Override
    public void deleteAll() {
        OrientGraph graph = orientGraphFactory.graph();
        for (Vertex vertex : graph.getVerticesOfClass(schema.getClassName())) {
            vertex.remove();
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        OrientGraph graph = orientGraphFactory.graph();
        List<T> result = new ArrayList<T>();
        int total = 0;
        int prevOffset = pageable.getOffset();
        int offset = pageable.getOffset() + pageable.getPageSize();
        for (Vertex vertex : graph.getVerticesOfClass(schema.getClassName())) {
            if (total >= prevOffset && total < offset) {
                result.add(schema.loadFromGraph(vertex));
            }
            total++;
        }
        return new PageImpl<T>(result, pageable, total);
    }

    @Override
    public Iterable<T> findAll() {
        OrientGraph graph = orientGraphFactory.graph();
        List<T> result = new ArrayList<T>();
        for (Vertex vertex : graph.getVerticesOfClass(schema.getClassName())) {
            result.add(schema.loadFromGraph(vertex));
        }
        return result;
    }

}
