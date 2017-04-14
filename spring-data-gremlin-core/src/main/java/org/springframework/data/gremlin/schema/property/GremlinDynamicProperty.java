package org.springframework.data.gremlin.schema.property;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.springframework.data.gremlin.schema.property.mapper.GremlinLinkPropertyMapper;

import java.util.Map;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinDynamicProperty<C extends Map> extends GremlinRelatedProperty<C> {

    private String relatedClassName;

    public GremlinDynamicProperty(Class<C> cls, String name, String relatedClassName, Direction direction) {
        super(cls, name, direction, new GremlinLinkPropertyMapper(), CARDINALITY.ONE_TO_ONE);
        this.relatedClassName = relatedClassName;
    }

    public String getRelatedClassName() {
        return relatedClassName;
    }
}
