package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinAdjacentProperty}s.
 *
 * @author Gman
 */
public class GremlinAdjacentPropertyMapper implements GremlinPropertyMapper<GremlinAdjacentProperty, Edge> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinAdjacentPropertyMapper.class);
    @Override
    public void copyToVertex(final GremlinAdjacentProperty property, final GremlinGraphAdapter graphAdapter, final Edge edge, final Object val, final  Map<Object, Object> cascadingSchemas) {

        edge.vertices(property.getDirection()).forEachRemaining(new Consumer<Vertex>() {
            @Override
            public void accept(Vertex vertex) {
                LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
                property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, vertex, val, cascadingSchemas);
            }
        });
    }

    @Override
    public <K> Object loadFromVertex(final GremlinAdjacentProperty property, final GremlinGraphAdapter graphAdapter, final Edge edge, final Map<Object, Object> cascadingSchemas) {
        final Object[] val = { null };
        edge.vertices(property.getDirection()).forEachRemaining(new Consumer<Vertex>() {
            @Override
            public void accept(Vertex vertex) {
                graphAdapter.refresh(vertex);
                val[0] = property.getRelatedSchema().cascadeLoadFromGraph(graphAdapter, vertex, cascadingSchemas);
            }
        });
        return val[0];
    }
}
