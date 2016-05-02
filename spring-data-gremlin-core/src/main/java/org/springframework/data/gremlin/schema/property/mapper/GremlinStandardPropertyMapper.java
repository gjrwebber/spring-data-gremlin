package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

import java.util.Map;

/**
 * A concrete {@link GremlinPropertyMapper} for mapping stadard Java types to Vertex properties.
 *
 * @author Gman
 */
public class GremlinStandardPropertyMapper implements GremlinPropertyMapper<GremlinProperty, Element> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinStandardPropertyMapper.class);

    @Override
    public void copyToVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Element element, Object val, Map<Object, Object> cascadingSchemas) {
        LOGGER.debug("Mapping property: " + property.getName() + " to element: " + element);
        element.setProperty(property.getName(), val);
    }

    @Override
    public <K> Object loadFromVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Element element, Map<Object, Object> cascadingSchemas) {
        return element.getProperty(property.getName());
    }
}
