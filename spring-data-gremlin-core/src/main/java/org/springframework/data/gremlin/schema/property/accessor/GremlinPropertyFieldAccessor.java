package org.springframework.data.gremlin.schema.property.accessor;

import org.springframework.data.gremlin.schema.property.GremlinProperty;

import java.lang.reflect.Field;

/**
 * Interface defining an accessor of a {@link GremlinProperty}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public interface GremlinPropertyFieldAccessor<V> extends GremlinPropertyAccessor<V> {
    Field getField();
}
