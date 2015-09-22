package org.springframework.data.gremlin.schema.generator;

import org.neo4j.graphdb.Direction;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by gman on 3/08/15.
 */
public class Neo4jSchemaGenerator extends BasicSchemaGenerator implements AnnotatedSchemaGenerator {

    /**
     * Returns the Vertex name. By default the Class' simple name is used. If it is annotated with @RelationshipEntity and the type parameter is
     * not empty, then that is used.
     *
     * @param clazz The Class to find the name of
     * @return The vertex name of the class
     */
    @Override
    protected String getVertexName(Class<?> clazz) {

        String className = super.getVertexName(clazz);
        RelationshipEntity entity = AnnotationUtils.getAnnotation(clazz, RelationshipEntity.class);
        if (entity != null && !StringUtils.isEmpty(entity.type())) {
            className = entity.type();
        }

        return className;
    }

    @Override
    protected Field getIdField(Class<?> cls) throws SchemaGeneratorException {
        final Field[] idFields = { null };

        ReflectionUtils.doWithFields(cls, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                GraphId id = AnnotationUtils.getAnnotation(field, GraphId.class);
                if (id != null) {
                    idFields[0] = field;
                }
            }
        });
        if (idFields[0] == null) {
            try {
                idFields[0] = super.getIdField(cls);
            } catch (SchemaGeneratorException e) {
                throw new SchemaGeneratorException("Cannot generate schema as there is no ID field. You must have a field of type Long or String annotated with @Id or named 'id'.");
            }
        }
        return idFields[0];
    }

    @Override
    protected Index.IndexType getIndexType(Field field) {
        Index.IndexType index = super.getIndexType(field);
        if (index == null || index == Index.IndexType.NONE) {

            Indexed indexed = AnnotationUtils.getAnnotation(field, Indexed.class);
            if (indexed != null) {
                if (indexed.unique()) {
                    index = Index.IndexType.UNIQUE;
                } else {
                    index = Index.IndexType.NON_UNIQUE;
                }
            } else {
                index = Index.IndexType.NONE;
            }
        }
        return index;
    }

    @Override
    protected String getPropertyName(Field field, Field rootEmbeddedField) {
        String name = super.getPropertyName(field, rootEmbeddedField);

        // If annotated with @GraphProperty, use the propertyName parameter of the annotation
        GraphProperty graphProperty = AnnotationUtils.getAnnotation(field, GraphProperty.class);

        if (graphProperty != null) {
            if (!StringUtils.isEmpty(graphProperty.propertyName())) {
                name = graphProperty.propertyName();
            }
        } else {
            RelatedTo relatedTo = AnnotationUtils.getAnnotation(field, RelatedTo.class);
            if (relatedTo != null) {

                if (!StringUtils.isEmpty(relatedTo.type())) {
                    name = relatedTo.type();
                }
            } else {
                RelatedToVia relatedToVia = AnnotationUtils.getAnnotation(field, RelatedToVia.class);
                if (relatedToVia != null) {
                    if (isAdjacentField(field.getType(), field)) {
                        String adjacentName = getVertexName(field.getType());
                        if (!adjacentName.equals(field.getType().getName())) {
                            name = adjacentName;
                        } else if (!StringUtils.isEmpty(relatedToVia.type())) {
                            name = relatedToVia.type();
                        }
                    }
                }
            }
        }

        return name;
    }

    @Override
    protected boolean isLinkField(Class<?> cls, Field field) {
        if (isVertexClass(cls)) {

            Annotation[] annotations = AnnotationUtils.getAnnotations(field);
            for (Annotation annotation : annotations) {
                if (annotation instanceof RelatedTo) {// || annotation instanceof StartNode || annotation instanceof EndNode) {
                    return true;
                }
            }
        }
        return false;
        //        return (isVertexClass(cls) || isEdgeClass(cls)) && ((AnnotationUtils.getAnnotation(field, RelatedTo.class) != null) || AnnotationUtils.getAnnotation(field, RelatedToVia
        // .class) !=
        //                                                                                                                                       null);
    }

    @Override
    protected boolean isLinkViaField(Class<?> cls, Field field) {
        if (isEdgeClass(cls)) {

            Annotation[] annotations = AnnotationUtils.getAnnotations(field);
            for (Annotation annotation : annotations) {
                if (annotation instanceof RelatedToVia) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isAdjacentField(Class<?> cls, Field field) {
        if (isVertexClass(cls)) {
            Annotation[] annotations = AnnotationUtils.getAnnotations(field);
            for (Annotation annotation : annotations) {
                if (annotation instanceof StartNode || annotation instanceof EndNode) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isAdjacentOutward(Class<?> cls, Field field) {
        StartNode startNode = AnnotationUtils.getAnnotation(field, StartNode.class);
        if (startNode != null) {
            return true;
        }

        EndNode endNode = AnnotationUtils.getAnnotation(field, EndNode.class);
        if (endNode != null) {
            return false;
        }

        return true;
    }

    @Override
    protected boolean isLinkOutward(Class<?> cls, Field field) {
        RelatedTo relatedTo = AnnotationUtils.getAnnotation(field, RelatedTo.class);
        if (relatedTo != null) {
            return relatedTo.direction() == Direction.OUTGOING;
        }
        StartNode startNode = AnnotationUtils.getAnnotation(field, StartNode.class);
        if (startNode != null) {
            return true;
        }

        EndNode endNode = AnnotationUtils.getAnnotation(field, EndNode.class);
        if (endNode != null) {
            return false;
        }

        return true;
    }

    @Override
    protected boolean isCollectionField(Class<?> cls, Field field) {
        return super.isCollectionField(cls, field) && AnnotationUtils.getAnnotation(field, RelatedTo.class) != null;
    }

    @Override
    protected boolean isCollectionViaField(Class<?> cls, Field field) {
        return super.isCollectionViaField(cls, field) && AnnotationUtils.getAnnotation(field, RelatedToVia.class) != null;
    }
    //    @Override
    //    protected boolean isLinkViaEdge(Class<?> cls, Field field) {
    //        return isEdgeClass(cls) && (AnnotationUtils.getAnnotation(field, RelatedToVia.class) != null);
    //    }

    @Override
    public Class<? extends Annotation> getVertexAnnotationType() {
        return NodeEntity.class;
    }

    @Override
    public Class<? extends Annotation> getEmbeddedAnnotationType() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getEdgeAnnotationType() {
        return RelationshipEntity.class;
    }
}
