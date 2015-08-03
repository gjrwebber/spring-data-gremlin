package org.springframework.data.gremlin.repository;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

/**
 * Base class for creating verticies and edges on the Graph. This class can be
 * subclassed for concrete implementations if need be.
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
        Vertex vertex = graph.addVertex(className);
        return vertex;
    }

    @Transactional(readOnly = true)
    public Vertex findVertexById(String id) {
        G graph = graphFactory.graph();
        Vertex playerVertex = null;
        Iterator<Vertex> it = graph.vertices(decodeId(id));
        if (it == null || !it.hasNext()) {
            it = graph.vertices(id);
        }

        if (it != null && it.hasNext()) {
            playerVertex = it.next();
        }
        return playerVertex;
    }

    @Transactional(readOnly = true)
    public Vertex getVertex(String id) {
        return graphFactory.graph().vertices(id).next();
    }

    @Transactional(readOnly = false)
    public Vertex createVertex(GremlinSchema schema) {
        return createVertex(schema.getClassName());
    }

    @Transactional(readOnly = false)
    public Edge addEdge(Object o, Vertex outVertex, Vertex inVertex, String name) {
        Edge edge = outVertex.addEdge(name, inVertex);
        return edge;
    }

    @Transactional(readOnly = false)
    public void removeEdge(Edge edge) {
        edge.remove();
    }

    @Transactional(readOnly = false)
    public void removeVertex(Vertex vertexToDelete) {
        vertexToDelete.remove();
    }

    public String encodeId(String id) {
        return id;
    }

    public String decodeId(String id) {
        return id;
    }

}
