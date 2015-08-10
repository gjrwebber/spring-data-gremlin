package org.springframework.data.gremlin.schema.generator;

import com.tinkerpop.blueprints.Direction;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinPropertyFactory;
import org.springframework.data.gremlin.schema.property.accessor.*;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.utils.GenericsUtil;
import org.springframework.util.ReflectionUtils;

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
public class DefaultSchemaGenerator implements SchemaGenerator {

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

    //    protected <S> GremlinSchema.SCHEMA_TYPE getSchemaType(Class<S> clazz) {
    //        if (entityClasses.contains(clazz)) {
    //            return GremlinSchema.SCHEMA_TYPE.ENTITY;
    //        } else if (embeddedClasses.contains(clazz)) {
    //            return GremlinSchema.SCHEMA_TYPE.EMBEDDED;
    //        } else {
    //            return GremlinSchema.SCHEMA_TYPE.ENTITY;
    //        }
    //    }

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
        return field != null && acceptType(field.getType()) && !schema.getIdAccessor().getField().equals(field) && !Modifier.isTransient(field.getModifiers());
    }

    protected Field getIdField(Class<?> cls) throws SchemaGeneratorException {
        try {
            Field field = ReflectionUtils.findField(cls, "id");
            if (field.getType() == Long.class || field.getType() == String.class) {
                return field;
            } else {
                throw new NoSuchFieldException("");
            }
        } catch (NoSuchFieldException e) {
            throw new SchemaGeneratorException("Cannot generate schema as there is no ID field. You must have a field of type Long or String named 'id'.");
        }
    }

    protected Class<?> getEnumType(Field field) {
        return Integer.class;
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
        String propertyName;

        propertyName = field.getName();

        if (rootEmbeddedField != null) {
            propertyName = String.format("%s_%s", getPropertyName(rootEmbeddedField, null), propertyName);
        }
        return propertyName;
    }

    protected boolean isEmbeddedField(Class<?> cls, Field field) {
        return isEmbeddedClass(cls);
    }

    protected boolean isLinkField(Class<?> cls, Field field) {
        return isEntityClass(cls);
    }

    protected boolean isLinkViaField(Class<?> cls, Field field) {
        return isRelationshipClass(cls);
    }

    protected boolean isAdjacentField(Class<?> cls, Field field) {
        return false;
    }

    protected boolean isAdjacentOutward(Class<?> cls, Field field) {
        return false;
    }

    //    protected boolean isLinkViaEdge(Class<?> cls, Field field) {
    //        return false;
    //    }

    protected boolean isLinkOutward(Class<?> cls, Field field) {
        return true;
    }

    protected boolean isCollectionField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isEntityClass(getCollectionType(field));
    }

    protected boolean isCollectionViaField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isRelationshipClass(getCollectionType(field));
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
        return clazz.getSimpleName();
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
        return Enum.class.isAssignableFrom(cls) || ClassUtils.isPrimitiveOrWrapper(cls) || cls == String.class || Collection.class.isAssignableFrom(cls) || cls == Date.class || isEntityClass(cls) ||
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


}
