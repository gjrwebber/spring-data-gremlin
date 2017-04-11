package org.springframework.data.gremlin.repository.orientdb;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.security.OIdentity;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
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

    @Override
    public Element refresh(Element element) {
        ((OrientElement) element).reload();
        return element;
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

    @Override
    public boolean isValidId(String id) {
        return super.isValidId(id) && !id.contains("-");
    }
}
