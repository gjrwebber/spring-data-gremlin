package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

/**
 * Defines mapping a {@link GremlinProperty} to a {@link Vertex}.
 *
 * @author Gman
 */
public interface GremlinPropertyMapper<E extends GremlinProperty> {

    void copyToVertex(E property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val);

    Object loadFromVertex(E property, Vertex vertex);
}
