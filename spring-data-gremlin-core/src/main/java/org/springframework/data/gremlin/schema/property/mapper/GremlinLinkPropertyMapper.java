package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;

import java.util.Set;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s.
 *
 * @author Gman
 */
public class GremlinLinkPropertyMapper implements GremlinPropertyMapper<GremlinLinkProperty> {

    private Direction direction;

    public GremlinLinkPropertyMapper(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void copyToVertex(GremlinLinkProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Set<GremlinSchema> cascadingSchemas) {

        Vertex linkedVertex = null;

        // get the current edge for this property
        Iterable<Edge> edges = vertex.getEdges(direction, property.getName());
        if (edges.iterator().hasNext()) {
            Edge edge = edges.iterator().next();
            linkedVertex = edge.getVertex(direction.opposite());
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
            if(direction == Direction.OUT) {
                graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
            } else {
                graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
            }
        }

            // Updates or saves the val into the linkedVertex
            cascadingSchemas.add(property.getSchema());
            property.getRelatedSchema().cascadeCopyToVertex(graphAdapter, linkedVertex, val, cascadingSchemas, property.getSchema());
    }

    @Override
    public Object loadFromVertex(GremlinLinkProperty property, Vertex vertex, Set<GremlinSchema> cascadingSchemas) {

        Object val = null;
        for (Edge outEdge : vertex.getEdges(direction, property.getName())) {
            Vertex inVertex = outEdge.getVertex(direction.opposite());
            val = property.getRelatedSchema().cascadeLoadFromVertex(inVertex, cascadingSchemas, property.getSchema());
        }

        return val;
    }
}
