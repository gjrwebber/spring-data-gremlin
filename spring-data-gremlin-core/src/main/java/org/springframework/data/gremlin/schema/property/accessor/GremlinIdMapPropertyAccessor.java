package org.springframework.data.gremlin.schema.property.accessor;

/**
 *
 * @author Gman
 */
public class GremlinIdMapPropertyAccessor extends GremlinMapPropertyAccessor<String> implements GremlinIdPropertyAccessor {

    public GremlinIdMapPropertyAccessor() {
        super("_id_");
    }

}
