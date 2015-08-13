package org.springframework.data.gremlin.schema.generator;

import com.tinkerpop.blueprints.Direction;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.*;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinPropertyFactory;
import org.springframework.data.gremlin.schema.property.accessor.*;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.utils.GenericsUtil;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Default {@link SchemaGenerator} using Java reflection along with Index and Index annotations.
 * <p>
 * This class can and should be extended for custom generation.
 * </p>
 *
 * @author Gman
 */
public class DefaultSchemaGenerator implements SchemaGenerator, AnnotatedSchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSchemaGenerator.class);
    private Set<Class<?>> entityClasses;
    private Set<Class<?>> embeddedClasses;
    private Set<Class<?>> relationshipClasses;
    private GremlinPropertyFactory propertyFactory;
    private GremlinPropertyEncoder idEncoder;

    public DefaultSchemaGenerator() {
        this(null, new GremlinPropertyFactory());
    }

    public DefaultSchemaGenerator(GremlinPropertyEncoder idEncoder, GremlinPropertyFactory propertyFactory) {
        this.idEncoder = idEncoder;
        this.propertyFactory = propertyFactory;
    }

    /**
     * @param clazz The Class to create a GremlinSchema from
     * @return A GremlinSchema for the given Class
     */
    public <V> GremlinSchema<V> generateSchema(Class<V> clazz) throws SchemaGeneratorException {

        String className = getVertexName(clazz);

        Field field = getIdField(clazz);
        GremlinIdFieldPropertyAccessor idAccessor = new GremlinIdFieldPropertyAccessor(field);

        GremlinSchema<V> schema = new GremlinSchema<V>(clazz);
        schema.setClassName(className);
        schema.setClassType(clazz);
        //        schema.setSchemaType(getSchemaType(clazz));
        schema.setWritable(isSchemaWritable(clazz));
        schema.setIdAccessor(idAccessor);
        schema.setIdEncoder(idEncoder);

        // Generate the Schema for clazz with all of it's super classes.
        populate(clazz, schema);
        if (schema.isWritable() && schema.getIdAccessor() == null) {
            throw new SchemaGeneratorException("Could not generate Schema for " + clazz.getSimpleName() + ". No @Id field found.");
        }
        return schema;
    }

    protected <S> boolean isSchemaWritable(Class<S> clazz) {
        return isEntityClass(clazz);
    }

    protected <V, S> void populate(Class<V> clazz, GremlinSchema<S> schema) {
        populate(clazz, schema, null);
    }

    /**
     * @param clazz
     * @param schema
     * @param embeddedFieldAccessor If this method was called for an embedded Field it is provided here
     */
    protected <V, S> void populate(final Class<V> clazz, final GremlinSchema<S> schema, final GremlinFieldPropertyAccessor embeddedFieldAccessor) {

        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {DefaultSchemaGenerator.this.processField(field, schema, embeddedFieldAccessor);}
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                boolean result = shouldProcessField(schema, field);
                return result;
            }
        });
    }

    protected <S> void processField(Field field, GremlinSchema<S> schema, GremlinFieldPropertyAccessor embeddedFieldAccessor) {


        Class<?> cls = field.getType();

        GremlinPropertyAccessor accessor;
        GremlinProperty property = null;

        // Get the rootEmbeddedField
        Field rootEmbeddedField = null;
        if (embeddedFieldAccessor != null) {
            rootEmbeddedField = embeddedFieldAccessor.getRootField();
        }
        String name = getPropertyName(field, rootEmbeddedField);

        // Check if we accept this standard type
        if (acceptType(cls)) {

            // If it is an enum, check if it is annotated with @Enumerated
            if (cls.isEnum()) {
                Class<?> enumType = cls;
                cls = getEnumType(field);
                if (cls == String.class) {
                    accessor = new GremlinEnumStringFieldPropertyAccessor(field, enumType);
                } else {
                    accessor = new GremlinEnumOrdinalFieldPropertyAccessor(field, enumType);
                }
            } else {
                accessor = new GremlinFieldPropertyAccessor(field, embeddedFieldAccessor);
                if (isCollectionViaField(cls, field)) {
                    cls = getCollectionType(field);
                    if (isLinkOutward(cls, field)) {
                        property = propertyFactory.getCollectionViaProperty(cls, name, Direction.OUT);
                    } else {
                        property = propertyFactory.getCollectionViaProperty(cls, name, Direction.IN);
                    }
                } else if (isCollectionField(cls, field)) {
                    cls = getCollectionType(field);
                    if (isLinkOutward(cls, field)) {
                        property = propertyFactory.getCollectionProperty(cls, name, Direction.OUT);
                    } else {
                        property = propertyFactory.getCollectionProperty(cls, name, Direction.IN);
                    }
                } else if (isLinkViaField(cls, field)) {
                    if (isLinkOutward(cls, field)) {
                        property = propertyFactory.getLinkViaProperty(cls, name, Direction.OUT);
                    } else {
                        property = propertyFactory.getLinkViaProperty(cls, name, Direction.IN);
                    }
                } else if (isAdjacentField(cls, field)) {
                    if (isAdjacentOutward(cls, field)) {
                        property = propertyFactory.getAdjacentProperty(cls, name, Direction.OUT);
                    } else {
                        property = propertyFactory.getAdjacentProperty(cls, name, Direction.IN);
                    }
                } else if (isLinkField(cls, field)) {

                    if (isLinkOutward(cls, field)) {
                        property = propertyFactory.getLinkProperty(cls, name, Direction.OUT);
                    } else {
                        property = propertyFactory.getLinkProperty(cls, name, Direction.IN);
                    }
                } else if (isEmbeddedField(cls, field)) {
                    populate(cls, schema, (GremlinFieldPropertyAccessor) accessor);

                    // Return now as we don't want a property for the embedded field.
                    return;
                }
            }

            // Create the property if it hasn't been created already
            if (property == null) {
                Index.IndexType index = getIndexType(field);
                String indexName = null;
                if (index == Index.IndexType.NON_UNIQUE) {
                    indexName = getIndexName(field);
                }
                property = propertyFactory.getIndexedProperty(cls, name, index, indexName);
            }
            property.setAccessor(accessor);
            schema.addProperty(property);
        }
    }

    protected Index.IndexType getIndexType(Field field) {
        Index index = AnnotationUtils.getAnnotation(field, Index.class);
        if (index != null) {
            return index.type();
        } else {
            return Index.IndexType.NONE;
        }
    }

    protected String getIndexName(Field field) {
        return null;
    }

    protected boolean shouldProcessField(GremlinSchema schema, Field field) {
        boolean shouldProcessField =  field != null && acceptType(field.getType()) && !schema.getIdAccessor().getField().equals(field) && !Modifier.isTransient(field.getModifiers());

        boolean noTransientAnnotation = AnnotationUtils.getAnnotation(field, Ignore.class) == null;
        return shouldProcessField && noTransientAnnotation;
    }

    protected Field getIdField(Class<?> cls) throws SchemaGeneratorException {
        final Field[] idFields = { null };

        ReflectionUtils.doWithFields(cls, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                Id id = AnnotationUtils.getAnnotation(field, Id.class);
                if (id != null) {
                    idFields[0] = field;
                }
            }
        });
        if (idFields[0] == null) {
            Field field = ReflectionUtils.findField(cls, "id");

            if (idFields[0].getType() == Long.class || idFields[0].getType() == String.class) {
                idFields[0] = field;
            }


        }

        if (idFields[0] == null) {
            throw new SchemaGeneratorException("Cannot generate schema as there is no ID field. You must have a field of type Long or String annotated with @Id or named 'id'.");
        }

        return idFields[0];

    }

    protected Class<?> getEnumType(Field field) {
        Class<?> type = String.class;
        Enumerated enumerated = AnnotationUtils.getAnnotation(field, Enumerated.class);
        if (enumerated != null) {
            return enumerated.value().getType();
        }
        return type;
    }

    protected boolean isPropertyIndexed(Field field) {
        return AnnotationUtils.getAnnotation(field, Index.class) != null;
    }

    protected boolean isSpatialLatitudeIndex(Field field) {
        Index index = AnnotationUtils.getAnnotation(field, Index.class);
        if (index != null) {
            return index.type() == Index.IndexType.SPATIAL_LATITUDE;
        }
        return false;
    }

    protected boolean isSpatialLongitudeIndex(Field field) {
        Index index = AnnotationUtils.getAnnotation(field, Index.class);
        if (index != null) {
            return index.type() == Index.IndexType.SPATIAL_LONGITUDE;
        }
        return false;
    }

    protected boolean isPropertyUnique(Field field) {
        Index index = AnnotationUtils.getAnnotation(field, Index.class);
        if (index != null) {
            return index.type() == Index.IndexType.UNIQUE;
        }
        return false;
    }

    protected String getPropertyName(Field field, Field rootEmbeddedField) {
        Property property = AnnotationUtils.getAnnotation(field, Property.class);

        if (rootEmbeddedField != null) {

            PropertyOverride override = checkPropertyOverrides(rootEmbeddedField, field);
            if (override != null) {
                property = override.property();
            }
        }

        String annotationName = null;

        if (property != null) {
            annotationName = !StringUtils.isEmpty(property.value()) ? property.value() : property.name();
        }

        String propertyName = !StringUtils.isEmpty(annotationName) ? annotationName : field.getName();


        return propertyName;
    }

    private PropertyOverride checkPropertyOverrides(Field embeddedField, Field field) {

        PropertyOverride propertyOverride = null;
        Embed embed = AnnotationUtils.getAnnotation(embeddedField, Embed.class);
        if (embed != null) {
            for (PropertyOverride po : embed.propertyOverrides()) {
                propertyOverride = checkPropertyOverride(field, po);
                if (propertyOverride != null) {
                    break;
                }
            }
        }
        return propertyOverride;
    }

    private PropertyOverride checkPropertyOverride(Field embeddedField, Field field) {

        PropertyOverride propertyOverride = embeddedField.getAnnotation(PropertyOverride.class);
        return checkPropertyOverride(field, propertyOverride);
    }

    private PropertyOverride checkPropertyOverride(Field field, PropertyOverride propertyOverride) {
        if (propertyOverride != null && !StringUtils.isEmpty(propertyOverride.name()) && propertyOverride.property() != null) {
            if (field.getName().equals(propertyOverride.name())) {
                return propertyOverride;
            }
        }
        return null;
    }

    protected boolean isEmbeddedField(Class<?> cls, Field field) {
        return isEmbeddedClass(cls) && AnnotationUtils.getAnnotation(field, Embed.class) != null;
    }

    protected boolean isLinkField(Class<?> cls, Field field) {
        return isEntityClass(cls) && (AnnotationUtils.getAnnotation(field, Link.class) != null);
    }

    protected boolean isLinkViaField(Class<?> cls, Field field) {
        return isRelationshipClass(cls) && (AnnotationUtils.getAnnotation(field, LinkVia.class) != null);
    }

    protected boolean isAdjacentField(Class<?> cls, Field field) {
        return isEntityClass(cls) && (AnnotationUtils.getAnnotation(field, ToVertex.class) != null || AnnotationUtils.getAnnotation(field, FromVertex.class) != null);
    }

    protected boolean isAdjacentOutward(Class<?> cls, Field field) {
        FromVertex startNode = AnnotationUtils.getAnnotation(field, FromVertex.class);
        if (startNode != null) {
            return false;
        }

        ToVertex endNode = AnnotationUtils.getAnnotation(field, ToVertex.class);
        if (endNode != null) {
            return true;
        }

        return true;
    }

    protected boolean isLinkOutward(Class<?> cls, Field field) {
        Link relatedTo = AnnotationUtils.getAnnotation(field, Link.class);
        if (relatedTo != null) {
            return relatedTo.direction() == Direction.OUT;
        }
        FromVertex startNode = AnnotationUtils.getAnnotation(field, FromVertex.class);
        if (startNode != null) {
            return true;
        }

        ToVertex endNode = AnnotationUtils.getAnnotation(field, ToVertex.class);
        if (endNode != null) {
            return false;
        }

        return true;
    }

    protected boolean isCollectionField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isEntityClass(getCollectionType(field)) && AnnotationUtils.getAnnotation(field, Link.class) != null;
    }

    protected boolean isCollectionViaField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isRelationshipClass(getCollectionType(field)) && AnnotationUtils.getAnnotation(field, LinkVia.class) != null;
    }

    private Class<?> getCollectionType(Field field) {
        return GenericsUtil.getGenericType(field);
    }

    /**
     * Returns the Vertex name. By default the Class' simple name is used.
     *
     * @param clazz The Class to find the name of
     * @return The vertex name of the class
     */
    protected String getVertexName(Class<?> clazz) {
        Vertex vertex = AnnotationUtils.getAnnotation(clazz, Vertex.class);
        String vertexName = null;
        if (vertex != null) {
            vertexName = !StringUtils.isEmpty(vertex.value()) ? vertex.value() : vertex.name();
        }

        return !StringUtils.isEmpty(vertexName) ? vertexName : clazz.getSimpleName();
    }

    /**
     * @param cls
     * @return
     */
    protected boolean isEntityClass(Class<?> cls) {
        if (entityClasses == null) {
            LOGGER.warn("Entities is null, this is unusual and is possibly an error. Please add the entity classes to the concrete SchemaBuilder.");
            return false;
        }

        return entityClasses.contains(cls);
    }

    /**
     * @param cls
     * @return
     */
    protected boolean isEmbeddedClass(Class<?> cls) {
        if (embeddedClasses == null) {
            return false;
        }

        return embeddedClasses.contains(cls);
    }

    /**
     * @param cls
     * @return
     */
    protected boolean isRelationshipClass(Class<?> cls) {
        if (relationshipClasses == null) {
            return false;
        }

        return relationshipClasses.contains(cls);
    }

    protected boolean acceptType(Class<?> cls) {
        return Enumerated.class.isAssignableFrom(cls) || ClassUtils.isPrimitiveOrWrapper(cls) || cls == String.class || Collection.class.isAssignableFrom(cls) || cls == Date.class || isEntityClass(
                cls) ||
               isEmbeddedClass(cls) || isRelationshipClass(cls);
    }

    @Override
    public void setEntityClasses(Set<Class<?>> entityClasses) {
        this.entityClasses = entityClasses;
    }

    @Override
    public void setEntityClasses(Class<?>... entites) {
        setEntityClasses(new HashSet<Class<?>>(Arrays.asList(entites)));
    }

    @Override
    public void setEmbeddedClasses(Set<Class<?>> embeddedClasses) {
        this.embeddedClasses = embeddedClasses;
    }

    @Override
    public void setEmbeddedClasses(Class<?>... embedded) {
        setEmbeddedClasses(new HashSet<Class<?>>(Arrays.asList(embedded)));
    }

    @Override
    public void setRelationshipClasses(Set<Class<?>> relationshipClasses) {
        this.relationshipClasses = relationshipClasses;
    }

    @Override
    public void setRelationshipClasses(Class<?>... relationshipClasses) {
        setRelationshipClasses(new HashSet<Class<?>>(Arrays.asList(relationshipClasses)));
    }

    @Override
    public Class<? extends Annotation> getEntityAnnotationType() {
        return Vertex.class;
    }

    @Override
    public Class<? extends Annotation> getEmbeddedAnnotationType() {
        return Embeddable.class;
    }

    @Override
    public Class<? extends Annotation> getRelationshipAnnotationType() {
        return Edge.class;
    }
}
