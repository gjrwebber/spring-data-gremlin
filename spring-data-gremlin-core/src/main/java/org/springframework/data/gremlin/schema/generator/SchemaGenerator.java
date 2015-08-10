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

    void setEntityClasses(Set<Class<?>> entities);

    void setEntityClasses(Class<?>... entites);

    void setEmbeddedClasses(Set<Class<?>> embedded);

    void setEmbeddedClasses(Class<?>... embedded);

    void setRelationshipClasses(Set<Class<?>> relationshipClasses);

    void setRelationshipClasses(Class<?>... relationshipClasses);
}
