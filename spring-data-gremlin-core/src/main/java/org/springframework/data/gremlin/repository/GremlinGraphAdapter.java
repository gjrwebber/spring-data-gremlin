package org.springframework.data.gremlin.repository;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Iterator;

/**
 * Base class for creating verticies and edges on the Graph. This class can be
 * subclassed for concrete implementations if need be.
 */
public class GremlinGraphAdapter<G extends Graph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinGraphAdapter.class);

    @Autowired
    protected GremlinGraphFactory<G> graphFactory;

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
    public Vertex findOrCreateVertex(String id, String className) {
        Vertex playerVertex = findVertexById(id);
        if (playerVertex == null) {
            playerVertex = createVertex(className);
        }
        return playerVertex;
    }

    @Transactional(readOnly = true)
    public Vertex findVertexById(String id) {
        if (id == null) {
            return null;
        }
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

    public Element refresh(Element element) {
        return element;
    }

    @Transactional(readOnly = true)
    public Edge findEdgeById(String id) {
        G graph = graphFactory.graph();
        Edge edge = null;
        Iterator<Edge> it = graph.edges(decodeId(id));
        if (it == null || !it.hasNext()) {
            it = graph.edges(id);
        }
        if (it != null && it.hasNext()) {
            edge = it.next();
        }
        return edge;
    }

    /**
     * Assumes the Vertex exists
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Vertex getVertex(String id) {
        if (id == null) {
            return null;
        }        
        return graphFactory.graph().vertices(id).next();
    }

    /**
     * Assumes the Edge exists
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Edge getEdge(String id) {
        if (id == null) {
            return null;
        }
        return graphFactory.graph().edges(id).next();
    }

    @Transactional(readOnly = false)
    public Vertex createVertex(GremlinSchema schema) {
        return createVertex(schema.getClassName());
    }

    @Transactional(readOnly = false)
    public Edge addEdge(Object o, Vertex outVertex, Vertex inVertex, String name) {
        LOGGER.debug("Creating edge " + outVertex + " -> " + inVertex + "...");
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

    public boolean isValidId(String id) {
        return !StringUtils.isEmpty(id);
    }
}
