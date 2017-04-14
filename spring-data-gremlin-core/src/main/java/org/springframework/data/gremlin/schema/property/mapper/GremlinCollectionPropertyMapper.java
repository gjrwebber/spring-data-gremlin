package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;

/**
 * A concrete {@link GremlinPropertyMapper} mapping a vertices property to a Collection.
 *
 * @author Gman
 */
public class GremlinCollectionPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinCollectionPropertyMapper.class);

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {


        // Get the Set of existing linked vertices for this property
        Set<Vertex> existingLinkedVertices = new HashSet<Vertex>();
        Set<Vertex> actualLinkedVertices = new HashSet<Vertex>();
        Iterator<Edge> iter = vertex.edges(property.getDirection(), property.getName());

        while (iter.hasNext()) {
            Edge currentEdge = iter.next();
            existingLinkedVertices.add(currentEdge.vertices(property.getDirection().opposite()).next());
        }

        // Now go through the collection of linked Objects
        for (Object linkedObj : (Collection) val) {

            // Find the linked vertex mapped to this linked Object
            Vertex linkedVertex = (Vertex) cascadingSchemas.get(linkedObj);
            if (linkedVertex == null) {
                String id = property.getRelatedSchema().getGraphId(linkedObj);
                if (id != null) {
                    linkedVertex = graphAdapter.getVertex(id);
                }
                if (linkedVertex == null) {
                    LOGGER.debug("No Linked Vertex for property: " + property.getName() + ". Creating " + property.getRelatedSchema().getClassName());

                    // No linked vertex yet, create it
                    linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
                }
            }

            Assert.notNull(linkedVertex);
            // If this linked Object is new it will not be in the existingLinkedVertices Set
            if (!existingLinkedVertices.contains(linkedVertex)) {
                // New linked Object - add an Edge
                if (property.getDirection() == Direction.OUT) {
                    graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
                } else {
                    graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
                }
                // Add to existingLinkedVertices so to not delete it later on when cascading deletes
                existingLinkedVertices.add(linkedVertex);
            }

            // Add the linkedVertex to the actual linked vertices.
            actualLinkedVertices.add(linkedVertex);

            if (Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT) {
                LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
                // Updates or saves the linkedObj into the linkedVertex
                property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, linkedObj, cascadingSchemas);
            }
        }

        // For each disjointed vertex, remove it and the Edge associated with this property
        for (Vertex vertexToDelete : CollectionUtils.disjunction(existingLinkedVertices, actualLinkedVertices)) {
            Iterator<Edge> edgeIter = vertexToDelete.edges(property.getDirection().opposite(), property.getName());
            while(edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                graphAdapter.removeEdge(edge);
            }
            graphAdapter.removeVertex(vertexToDelete);
        }
    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, graphAdapter, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(final GremlinSchema<V> schema, final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Map<Object, Object> cascadingSchemas) {
        final Set<V> collection = new HashSet<V>();
        vertex.edges(Direction.IN, property.getName()).forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge outEdge) {
                graphAdapter.refresh(outEdge);
                Vertex inVertex = outEdge.outVertex();
                graphAdapter.refresh(inVertex);
                V linkedObject = schema.cascadeLoadFromGraph(graphAdapter, inVertex, cascadingSchemas);
                collection.add(linkedObject);
            }
        });
        return collection;
    }
}