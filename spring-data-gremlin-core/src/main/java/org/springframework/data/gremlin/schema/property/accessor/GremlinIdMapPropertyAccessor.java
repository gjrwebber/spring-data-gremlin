package org.springframework.data.gremlin.schema.property.accessor;

/**
 * Base {@link GremlinPropertyAccessor}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public class GremlinIdMapPropertyAccessor extends GremlinMapPropertyAccessor<String> implements GremlinIdPropertyAccessor {

    public GremlinIdMapPropertyAccessor() {
        super("_id_", String.class, null);
    }

}
