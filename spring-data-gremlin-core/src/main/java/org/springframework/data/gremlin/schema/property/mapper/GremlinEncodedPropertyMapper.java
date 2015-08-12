package org.springframework.data.gremlin.schema.property.mapper;


import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;

import java.util.Map;

/**
 * An extended {@link GremlinStandardPropertyMapper} for mapping custom encoded properties.
 *
 * @author Gman
 */
public class GremlinEncodedPropertyMapper extends GremlinStandardPropertyMapper {

    private GremlinPropertyEncoder propertyEncoder;

    public GremlinEncodedPropertyMapper() {
    }

    public GremlinEncodedPropertyMapper(GremlinPropertyEncoder propertyEncoder) {
        this.propertyEncoder = propertyEncoder;
    }

    @Override
    public void copyToVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Element element, Object val, Map<Object, Object> cascadingSchemas) {
        Object id = val;
        if (propertyEncoder != null) {
            id = propertyEncoder.decode(val);
        }
        super.copyToVertex(property, graphAdapter, element, id, cascadingSchemas);
    }

    @Override
    public <K> Object loadFromVertex(GremlinProperty property, Element element, Map<Object, Object> cascadingSchemas) {
        Object id = super.loadFromVertex(property, element, cascadingSchemas);
        if (propertyEncoder != null) {
            id = propertyEncoder.encode(id);
        }
        return id;
    }


}
