package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s. There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge. If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with this property mapper</li>
 * </ul>
 *
 * @author Gman
 */
public class GremlinLinkPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinLinkPropertyMapper.class);

    @Override
    public void copyToVertex(final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Object val, final Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = null;

        // get the current edge for this property
        Iterator<Edge> edges = vertex.edges(property.getDirection(), property.getName());
        if (edges.hasNext()) {
            Edge edge = edges.next();
            linkedVertex = edge.vertices(property.getDirection().opposite()).next();
        } else {
            // No current edge, get it
            linkedVertex = (Vertex) cascadingSchemas.get(val);
            if (linkedVertex == null) {
                String id = property.getRelatedSchema().getGraphId(val);
                if (id != null) {
                    linkedVertex = graphAdapter.getVertex(id);
                }
            }

            if (linkedVertex == null) {
                LOGGER.debug("No Linked Vertex for property: " + property.getName() + ". Creating " + property.getRelatedSchema().getClassName());
                // No linked vertex yet, create it
                linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
            }

            Assert.notNull(linkedVertex);
            if (property.getDirection() == Direction.OUT) {
                graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
            } else {
                graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
            }
        }

        if (Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT) {
            LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
            // Updates or saves the val into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
        }
    }

    @Override
    public <K> Object loadFromVertex(final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Map<Object, Object> cascadingSchemas) {

        final Object[] val = { null };
        vertex.edges(property.getDirection(), property.getName()).forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge outEdge) {
                graphAdapter.refresh(outEdge);
                Vertex inVertex = outEdge.inVertex();
                graphAdapter.refresh(inVertex);
                val[0] = property.getRelatedSchema().cascadeLoadFromGraph(graphAdapter, inVertex, cascadingSchemas);
            }
        });
        return val[0];
    }
}
