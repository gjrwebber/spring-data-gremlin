package org.springframework.data.gremlin.utils;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created by mmichail (zifnab87) on 4/14/2017.
 */
public class GraphUtil {

    public static String queryToString(Graph graph, GraphTraversal traversal) {
        if (traversal == null) {
            return "";
        }
        return org.apache.tinkerpop.gremlin.groovy.jsr223.GroovyTranslator.of(graph.toString()).translate((traversal.asAdmin().clone()).getBytecode());
    }
}
