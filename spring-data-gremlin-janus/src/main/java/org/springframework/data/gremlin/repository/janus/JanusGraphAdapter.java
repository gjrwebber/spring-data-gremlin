package org.springframework.data.gremlin.repository.janus;

import org.janusgraph.core.JanusGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */
public class JanusGraphAdapter extends GremlinGraphAdapter<JanusGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JanusGraphAdapter.class);

    @Override
    @Transactional(readOnly = false)
    public Vertex createVertex(JanusGraph graph, String className) {
        Vertex vertex = graph.addVertex(className);
        return vertex;
    }

}