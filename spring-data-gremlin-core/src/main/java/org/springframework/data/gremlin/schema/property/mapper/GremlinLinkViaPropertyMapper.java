package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s. There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge. If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with this property mapper</li>
 * </ul>
 *
 * @author Gman
 */
public class GremlinLinkViaPropertyMapper extends GremlinLinkPropertyMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinLinkViaPropertyMapper.class);

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {

        GremlinAdjacentProperty adjacentProperty = property.getAdjacentProperty();

        // Check we found the adjacent property
        if (adjacentProperty != null) {

            Object adjacentObj = adjacentProperty.getAccessor().get(val);
            if (adjacentObj != null) {
                Vertex adjacentVertex = graphAdapter.findOrCreateVertex(adjacentProperty.getRelatedSchema().getObjectId(adjacentObj), adjacentProperty.getRelatedSchema().getClassName());


                // If we have the adjacent vertex then we can continue
                if (adjacentVertex != null) {

                    Edge linkedEdge = null;

                    // get the current edge for this property
                    Iterator<Edge> edges = vertex.getEdges(property.getDirection(), property.getRelatedSchema().getClassName()).iterator();
                    while (edges.hasNext()) {
                        Edge edge = edges.next();
                        if (edge.getVertex(property.getDirection().opposite()).equals(adjacentVertex)) {
                            linkedEdge = edge;
                            break;
                        }
                    }

                    if (linkedEdge == null) {
                        if (property.getDirection() == Direction.OUT) {
                            linkedEdge = graphAdapter.addEdge(null, vertex, adjacentVertex, property.getRelatedSchema().getClassName());
                        } else {
                            linkedEdge = graphAdapter.addEdge(null, adjacentVertex, vertex, property.getRelatedSchema().getClassName());
                        }
                        property.getRelatedSchema().copyToGraph(graphAdapter, linkedEdge, val, cascadingSchemas);
                    }

                    if(Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT) {
                        LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
                        // Updates or saves the val into the linkedVertex
                        property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedEdge, val, cascadingSchemas);
                    }
                }
            }
        }


    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Map<Object, Object> cascadingSchemas) {

        //        GremlinRelatedProperty adjacentProperty = getAdjacentProperty(property);

        Object val = null;
        for (Edge linkedEdge : vertex.getEdges(property.getDirection(), property.getRelatedSchema().getClassName())) {
            graphAdapter.refresh(linkedEdge);
            val = property.getRelatedSchema().cascadeLoadFromGraph(graphAdapter, linkedEdge, cascadingSchemas);
        }

        return val;
    }

}
