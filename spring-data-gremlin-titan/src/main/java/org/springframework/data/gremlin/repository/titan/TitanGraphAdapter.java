package org.springframework.data.gremlin.repository.titan;

import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gman on 27/06/15.
 */
public class TitanGraphAdapter extends GremlinGraphAdapter<TitanGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanGraphAdapter.class);

    @Override
    @Transactional(readOnly = false)
    public Vertex createVertex(TitanGraph graph, String className) {
        Vertex vertex = graph.addVertex(className);
        return vertex;
    }

}
