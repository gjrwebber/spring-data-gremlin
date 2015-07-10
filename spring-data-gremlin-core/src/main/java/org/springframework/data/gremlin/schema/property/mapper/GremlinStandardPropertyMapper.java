package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

/**
 * A concrete {@link GremlinPropertyMapper} for mapping stadard Java types to Vertex properties.
 *
 * @author Gman
 */
public class GremlinStandardPropertyMapper implements GremlinPropertyMapper<GremlinProperty> {

    @Override
    public void copyToVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val) {
        vertex.setProperty(property.getName(), val);
    }

    @Override
    public Object loadFromVertex(GremlinProperty property, Vertex vertex) {
        return vertex.getProperty(property.getName());
    }
}
