package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinCollectionProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A concrete {@link GremlinPropertyMapper} mapping a vertices property to a Collection.
 *
 * @author Gman
 */
public class GremlinCollectionInPropertyMapper implements GremlinPropertyMapper<GremlinCollectionProperty> {

    @Override
    public void copyToVertex(GremlinCollectionProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Set<GremlinSchema> cascadingSchemas) {


        // Get the Set of existing linked vertices for this property
        Set<Vertex> existingLinkedVertices = new HashSet<Vertex>();
        Set<Vertex> actualLinkedVertices = new HashSet<Vertex>();
        Iterator<Edge> edges = vertex.edges(Direction.IN, property.getName());
        while (edges.hasNext()) {
            existingLinkedVertices.add(edges.next().outVertex());
        }

        // Now go through the collection of linked Objects
        for (Object linkedObj : (Collection) val) {

            // Find the linked vertex mapped to this linked Object
            Vertex linkedVertex = null;
            String id = property.getRelatedSchema().getVertexId(linkedObj);
            if (id != null) {
                linkedVertex = graphAdapter.getVertex(id);
            }
            if (linkedVertex == null) {
                // No linked vertex yet, create it
                linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
            }

            // If this linked Object is new it will not be in the existingLinkedVertices Set
            if (!existingLinkedVertices.contains(linkedVertex)) {
                // New linked Object - add an Edge
                Edge edge = graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
                // Add to existingLinkedVertices so to not delete it later on when cascading deletes
                existingLinkedVertices.add(linkedVertex);
            }

            // Add the linkedVertex to the actual linked vertices.
            actualLinkedVertices.add(linkedVertex);

            // Updates or saves the linkedObj into the linkedVertex
            property.getRelatedSchema().cascadeCopyToVertex(graphAdapter, linkedVertex, linkedObj, cascadingSchemas, property.getSchema());
        }

        // For each disjointed vertex, remove it and the Edge associated with this property
        for (Vertex vertexToDelete : CollectionUtils.disjunction(existingLinkedVertices, actualLinkedVertices)) {
            Iterator<Edge> outEdges = vertex.edges(Direction.OUT, property.getName());
            while (outEdges.hasNext()) {
                graphAdapter.removeEdge(outEdges.next());
            }
            graphAdapter.removeVertex(vertexToDelete);
        }
    }

    @Override
    public Object loadFromVertex(GremlinCollectionProperty property, Vertex vertex, Set<GremlinSchema> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(GremlinSchema<V> schema, GremlinProperty property, Vertex vertex, Set<GremlinSchema> cascadingSchemas) {
        Set<V> collection = new HashSet<V>();
        Iterator<Edge> edges = vertex.edges(Direction.IN, property.getName());
        while (edges.hasNext()) {
            Vertex inVertex = edges.next().outVertex();
            V linkedObject = schema.cascadeLoadFromVertex(inVertex, cascadingSchemas, property.getSchema());
            collection.add(linkedObject);
        }
        return collection;
    }
}
