package org.springframework.data.gremlin.schema.generator;

import java.lang.annotation.Annotation;

/**
 * Interface defining an annotated {@link SchemaGenerator} providing the entity and embedded annotation types.
 *
 * @author Gman
 */
public interface AnnotatedSchemaGenerator extends SchemaGenerator {
    Class<? extends Annotation> getEntityAnnotationType();

    Class<? extends Annotation> getEmbeddedAnnotationType();

    Class<? extends Annotation> getRelationshipAnnotationType();
}
