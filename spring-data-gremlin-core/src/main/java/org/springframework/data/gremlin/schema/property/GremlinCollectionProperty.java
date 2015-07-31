package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.property.mapper.GremlinCollectionInPropertyMapper;

/**
 * A concrete {@link GremlinLinkToProperty} for a Collection
 *
 * @author Gman
 */
public class GremlinCollectionProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionProperty(Class<C> cls, String name) {
        super(cls, name, new GremlinCollectionInPropertyMapper(), CARDINALITY.ONE_TO_MANY);
    }
}
