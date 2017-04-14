package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

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
    public void copyToVertex(final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Object val, final Map<Object, Object> cascadingSchemas) {

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
                    Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
                    while (edges.hasNext()) {
                        Edge edge = edges.next();
                        if (edge.vertices(property.getDirection().opposite()).equals(adjacentVertex)) {
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
                    }

                    if(Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT) {
                        LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());

                        //TODO add but causes stackoverflow
                        //property.getRelatedSchema().copyToGraph(graphAdapter, linkedEdge, val);
                    }
                }
            }
        }


    }

    @Override
    public <K> Object loadFromVertex(final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Map<Object, Object> cascadingSchemas) {
        //        GremlinRelatedProperty adjacentProperty = getAdjacentProperty(property);

        Object val = null;
        Iterator<Edge> it = vertex.edges(property.getDirection(), property.getRelatedSchema().getClassName());
        while (it.hasNext()) {
            Edge linkedEdge = it.next();
            val = property.getRelatedSchema().cascadeLoadFromGraph(graphAdapter, linkedEdge, cascadingSchemas);
        }

        return val;
    }

}
