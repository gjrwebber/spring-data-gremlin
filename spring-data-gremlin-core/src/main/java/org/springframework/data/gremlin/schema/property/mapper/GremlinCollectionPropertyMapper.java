package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A concrete {@link GremlinPropertyMapper} mapping a vertices property to a Collection.
 *
 * @author Gman
 */
public class GremlinCollectionPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    @Override
    public void copyToVertex(final GremlinRelatedProperty property, final GremlinGraphAdapter graphAdapter, final Vertex vertex, final Object val, final Map<Object, Object> cascadingSchemas) {


        // Get the Set of existing linked vertices for this property
        final Set<Vertex> existingLinkedVertices = new HashSet<Vertex>();
        final Set<Vertex> actualLinkedVertices = new HashSet<Vertex>();
        vertex.edges(Direction.IN, property.getName()).forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge currentEdge) {
                existingLinkedVertices.add(currentEdge.outVertex());
            }
        });

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
                    // No linked vertex yet, create it
                    linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
                }
            }

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

            // Updates or saves the linkedObj into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, linkedObj, cascadingSchemas);
        }

        // For each disjointed vertex, remove it and the Edge associated with this property
        for (Vertex vertexToDelete : CollectionUtils.disjunction(existingLinkedVertices, actualLinkedVertices)) {
            vertexToDelete.edges(Direction.OUT, property.getName()).forEachRemaining(new Consumer<Edge>() {
                @Override
                public void accept(Edge edge) {
                    graphAdapter.removeEdge(edge);
                }
            });
            graphAdapter.removeVertex(vertexToDelete);
        }
    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(final GremlinSchema<V> schema, final GremlinRelatedProperty property, final Vertex vertex, final Map<Object, Object> cascadingSchemas) {
        final Set<V> collection = new HashSet<V>();
        vertex.edges(Direction.IN, property.getName()).forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge outEdge) {
                Vertex inVertex = outEdge.outVertex();
                V linkedObject = schema.cascadeLoadFromGraph(inVertex, cascadingSchemas);
                collection.add(linkedObject);
            }
        });
        return collection;
    }
}
