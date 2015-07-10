package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.property.mapper.GremlinLinkPropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinLinkProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkProperty(Class<C> cls, String name) {
        super(cls, name, new GremlinLinkPropertyMapper());
    }
}
