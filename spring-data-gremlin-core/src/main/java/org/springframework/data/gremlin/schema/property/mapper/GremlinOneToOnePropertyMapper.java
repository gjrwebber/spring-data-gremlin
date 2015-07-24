package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinOneToOneProperty;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinOneToOneProperty}s.
 *
 * @author Gman
 */
public class GremlinOneToOnePropertyMapper implements GremlinPropertyMapper<GremlinOneToOneProperty> {

    @Override
    public void copyToVertex(GremlinOneToOneProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val) {

        Vertex linkedVertex = null;

        // get the current edge for this property
        Iterable<Edge> edges = vertex.getEdges(Direction.OUT, property.getName());
        if (edges.iterator().hasNext()) {
            Edge edge = edges.iterator().next();
            linkedVertex = edge.getVertex(Direction.IN);
        } else {
            // No current edge, get it
            String id = property.getRelatedSchema().getVertexId(val);
            if (id != null) {
                linkedVertex = graphAdapter.getVertex(id);
            }
            if (linkedVertex == null) {
                // No linked vertex yet, create it
                linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
            }
            graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
        }

        // Updates or saves the val into the linkedVertex
        property.getRelatedSchema().copyToVertex(graphAdapter, linkedVertex, val);
    }

    @Override
    public Object loadFromVertex(GremlinOneToOneProperty property, Vertex vertex) {

        Object val = null;
        for (Edge outEdge : vertex.getEdges(Direction.OUT, property.getName())) {
            Vertex inVertex = outEdge.getVertex(Direction.IN);
            val = property.getRelatedSchema().loadFromVertex(inVertex);
        }

        return val;
    }
}
