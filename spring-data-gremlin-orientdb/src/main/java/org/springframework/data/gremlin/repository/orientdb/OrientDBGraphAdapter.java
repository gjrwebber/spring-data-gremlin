package org.springframework.data.gremlin.repository.orientdb;

import org.apache.tinkerpop.gremlin.orientdb.OrientGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.schema.property.encoder.orientdb.OrientDbIdEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gman on 27/06/15.
 */
public class OrientDBGraphAdapter extends GremlinGraphAdapter<OrientGraph> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDBGraphAdapter.class);

    private GremlinPropertyEncoder idEncoder = new OrientDbIdEncoder();

    @Override
    @Transactional(readOnly = false)
    public Vertex createVertex(OrientGraph graph, String className) {
        String classname = "class:" + className;
        Vertex vertex = graph.addVertex(classname);
        return vertex;
    }

    public String encodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.encode(id).toString();
        }
        return id;
    }

    public String decodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.decode(id).toString();
        }
        return id;
    }

}
