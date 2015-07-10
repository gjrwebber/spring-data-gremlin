package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.property.mapper.GremlinCollectionPropertyMapper;

/**
 * A concrete {@link GremlinLinkProperty} for a Collection
 *
 * @author Gman
 */
public class GremlinCollectionProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionProperty(Class<C> cls, String name) {
        super(cls, name, new GremlinCollectionPropertyMapper());
    }
}
