package org.springframework.data.gremlin.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.springframework.data.gremlin.schema.property.mapper.GremlinLinkViaPropertyMapper;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinLinkViaProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkViaProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinLinkViaPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
