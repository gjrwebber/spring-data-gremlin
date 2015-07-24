package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.property.mapper.GremlinOneToOnePropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinOneToOneProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinOneToOneProperty(Class<C> cls, String name) {
        super(cls, name, new GremlinOneToOnePropertyMapper());
    }
}
