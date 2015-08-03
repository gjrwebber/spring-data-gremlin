package org.springframework.data.gremlin.repository.tinker;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.impls.tg.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gman on 27/06/15.
 */
public class TinkerGraphAdapter extends GremlinGraphAdapter<TinkerGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerGraphAdapter.class);

    @Override
    @Transactional(readOnly = false)
    public Vertex createVertex(TinkerGraph graph, String className) {
        LOGGER.info("CREATING VERTEX: " + className);
        Vertex vertex = graph.addVertex(null);
        vertex.setProperty("label", className);
        return vertex;
    }

}
