package org.springframework.data.gremlin.schema.generator;

import org.springframework.data.gremlin.schema.GremlinSchema;

import java.util.Set;

/**
 * An interface defining schema generators.
 *
 * @author Gman
 */
public interface SchemaGenerator {

    <V> GremlinSchema<V> generateSchema(Class<V> clazz) throws SchemaGeneratorException;

    void setEntities(Set<Class<?>> entities);

    void setEntities(Class<?>... entites);

    void setEmbedded(Set<Class<?>> embedded);

    void setEmbedded(Class<?>... embedded);
}
