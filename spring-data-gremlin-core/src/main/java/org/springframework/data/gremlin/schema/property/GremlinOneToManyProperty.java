package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.property.mapper.GremlinOneToManyPropertyMapper;

/**
 * A concrete {@link GremlinOneToOneProperty} for a Collection
 *
 * @author Gman
 */
public class GremlinOneToManyProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinOneToManyProperty(Class<C> cls, String name) {
        super(cls, name, new GremlinOneToManyPropertyMapper());
    }
}
