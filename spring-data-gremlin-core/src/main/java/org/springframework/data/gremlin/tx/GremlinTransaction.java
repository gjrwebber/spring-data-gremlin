package org.springframework.data.gremlin.tx;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created by gman on 4/05/15.
 */
public class GremlinTransaction {

    private Graph graph;

    public GremlinTransaction(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
