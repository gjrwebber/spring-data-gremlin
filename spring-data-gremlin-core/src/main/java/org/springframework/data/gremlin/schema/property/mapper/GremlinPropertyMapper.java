package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.property.GremlinProperty;

import java.util.Map;

/**
 * Defines mapping a {@link GremlinProperty} to a {@link Vertex}.
 *
 * @author Gman
 */
public interface GremlinPropertyMapper<E extends GremlinProperty, V extends Element> {

    String CASCADE_ALL_KEY = "sdg-cascade-all";

    void copyToVertex(E property, GremlinGraphAdapter graphAdapter, V element, Object val, Map<Object, Object> cascadingSchemas);

    <K> Object loadFromVertex(E property, GremlinGraphAdapter graphAdapter, V element, Map<Object, Object> cascadingSchemas);
}
