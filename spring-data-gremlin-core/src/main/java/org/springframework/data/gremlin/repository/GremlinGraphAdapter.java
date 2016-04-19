package org.springframework.data.gremlin.repository;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

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
        Vertex vertex = graph.addVertex(null);
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
        Vertex vertex = graph.getVertex(decodeId(id));
        if (vertex == null) {
            vertex = graph.getVertex(id);
        }
        return vertex;
    }

    public Element refresh(Element element) {
        return element;
    }

    @Transactional(readOnly = true)
    public Edge findEdgeById(String id) {
        G graph = graphFactory.graph();
        Edge edge = graph.getEdge(decodeId(id));
        if (edge == null) {
            edge = graph.getEdge(id);
        }
        return edge;
    }

    @Transactional(readOnly = true)
    public Vertex getVertex(String id) {
        if (id == null) {
            return null;
        }
        return graphFactory.graph().getVertex(id);
    }

    @Transactional(readOnly = true)
    public Edge getEdge(String id) {
        return graphFactory.graph().getEdge(id);
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
        graphFactory.graph().removeEdge(edge);
    }

    @Transactional(readOnly = false)
    public void removeVertex(Vertex vertexToDelete) {
        graphFactory.graph().removeVertex(vertexToDelete);
    }

    public String encodeId(String id) {
        return id;
    }

    public String decodeId(String id) {
        return id;
    }

}
