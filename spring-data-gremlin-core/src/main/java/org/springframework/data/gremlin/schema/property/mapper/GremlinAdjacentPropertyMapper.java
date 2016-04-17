package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;

import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinAdjacentProperty}s.
 *
 * @author Gman
 */
public class GremlinAdjacentPropertyMapper implements GremlinPropertyMapper<GremlinAdjacentProperty, Edge> {

    @Override
    public void copyToVertex(GremlinAdjacentProperty property, GremlinGraphAdapter graphAdapter, Edge edge, Object val, Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = edge.getVertex(property.getDirection());

        if (linkedVertex == null) {
            linkedVertex = (Vertex) cascadingSchemas.get(val);
        }

        if (linkedVertex != null && (Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT)) {
            //             Updates or saves the val into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
        }

    }

    @Override
    public <K> Object loadFromVertex(GremlinAdjacentProperty property, Edge edge, Map<Object, Object> cascadingSchemas) {
        Object val = null;
        Vertex linkedVertex = edge.getVertex(property.getDirection());
        if (linkedVertex != null) {
            val = property.getRelatedSchema().cascadeLoadFromGraph(linkedVertex, cascadingSchemas);
        }
        return val;
    }
}
