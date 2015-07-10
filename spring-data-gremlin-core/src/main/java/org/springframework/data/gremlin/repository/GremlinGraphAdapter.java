package org.springframework.data.gremlin.repository;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gman on 27/06/15.
 */
public class GremlinGraphAdapter<G extends Graph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinGraphAdapter.class);

    @Autowired
    private GremlinGraphFactory<G> graphFactory;

    @Transactional(readOnly = false)
    public Vertex createVertex(String className) {
        G graph = graphFactory.graph();
        return createVertex(graph, className);
    }

    @Transactional(readOnly = false)
    public Vertex createVertex(G graph, String className) {
        LOGGER.info("CREATING VERTEX: " + className);
        Vertex vertex = graph.addVertex(null);
        return vertex;
    }

    @Transactional(readOnly = true)
    public Vertex findVertexById(String id) {
        G graph = graphFactory.graph();
        Vertex playerVertex = graph.getVertex(decodeId(id));
        if (playerVertex == null) {
            playerVertex = graph.getVertex(id);
        }
        return playerVertex;
    }

    @Transactional(readOnly = true)
    public Vertex getVertex(String id) {
        return graphFactory.graph().getVertex(id);
    }

    @Transactional(readOnly = false)
    public Vertex createVertex(GremlinSchema schema) {
        return createVertex(schema.getClassName());
    }

    @Transactional(readOnly = false)
    public void addEdge(Object o, Vertex vertex, Vertex linkedVertex, String name) {
        graphFactory.graph().addEdge(null, vertex, linkedVertex, name);
    }

    @Transactional(readOnly = false)
    public void removeEdge(Edge edge) {
        graphFactory.graph().removeEdge(edge);
    }

    @Transactional(readOnly = false)
    public void removeVertex(Vertex vertexToDelete) {
        graphFactory.graph().removeVertex(vertexToDelete);
    }
    public <V> void copyToVertex(GremlinSchema<V> schema, Vertex vertex, Object obj) {
        for (GremlinProperty property : schema.getProperties()) {

            try {

                GremlinPropertyAccessor accessor = property.getAccessor();
                Object val = accessor.get(obj);

                if (val != null) {
                    property.copyToVertex(this, vertex, val);
                }
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()), e);
            }
        }
    }

    public <V> V loadFromVertex(GremlinSchema<V> schema, Vertex vertex) {

        V obj;
        try {
            obj = schema.getClassType().newInstance();

            GremlinPropertyAccessor idAccessor = schema.getIdAccessor();
            idAccessor.set(obj, encodeId(vertex.getId().toString()));
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate new " + schema.getClassType(), e);
        }
        for (GremlinProperty property : schema.getProperties()) {

            try {
                Object val = property.loadFromVertex(vertex);
                GremlinPropertyAccessor accessor = property.getAccessor();
                accessor.set(obj, val);
            } catch (Exception e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()));
            }
        }
        return obj;
    }

    public String encodeId(String id) {
        return id;
    }

    public String decodeId(String id) {
        return id;
    }

}
