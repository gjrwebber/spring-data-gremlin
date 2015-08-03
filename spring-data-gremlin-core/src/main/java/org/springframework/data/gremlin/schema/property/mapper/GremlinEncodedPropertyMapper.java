package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;

import java.util.Set;

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
    public void copyToVertex(GremlinProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Set<GremlinSchema> cascadingSchemas) {
        Object id = val;
        if (propertyEncoder != null) {
            id = propertyEncoder.decode(val);
        }
        super.copyToVertex(property, graphAdapter, vertex, id, cascadingSchemas);
    }

    @Override
    public Object loadFromVertex(GremlinProperty property, Vertex vertex, Set<GremlinSchema> cascadingSchemas) {
        Object id = super.loadFromVertex(property, vertex, cascadingSchemas);
        if (propertyEncoder != null) {
            id = propertyEncoder.encode(id);
        }
        return id;
    }


}
