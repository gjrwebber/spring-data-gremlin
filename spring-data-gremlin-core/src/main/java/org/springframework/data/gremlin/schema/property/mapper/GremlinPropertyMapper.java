package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

import java.util.Set;

/**
 * Defines mapping a {@link GremlinProperty} to a {@link Vertex}.
 *
 * @author Gman
 */
public interface GremlinPropertyMapper<E extends GremlinProperty> {

    void copyToVertex(E property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Set<GremlinSchema> cascadingSchemas);

    Object loadFromVertex(E property, Vertex vertex, Set<GremlinSchema> cascadingSchemas);
}
