package org.springframework.data.gremlin.schema.generator;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinPropertyFactory;
import org.springframework.data.gremlin.schema.property.accessor.GremlinEnumOrdinalFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinEnumStringFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;
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
    private Set<Class<?>> entities;
    private Set<Class<?>> embedded;
    private GremlinPropertyFactory propertyFactory = new GremlinPropertyFactory();
    private GremlinPropertyEncoder idEncoder;

    public DefaultSchemaGenerator() {
        this(null);
    }

    public DefaultSchemaGenerator(GremlinPropertyEncoder idEncoder) {
        this.idEncoder = idEncoder;
    }

    /**
     * @param clazz The Class to create a GremlinSchema from
     * @return A GremlinSchema for the given Class
     */
    public <V> GremlinSchema<V> generateSchema(Class<V> clazz) throws SchemaGeneratorException {

        String className = getVertexName(clazz);

        Field field = getIdField(clazz);
        GremlinFieldPropertyAccessor<String> idAccessor = new GremlinFieldPropertyAccessor<String>(field);

        GremlinSchema<V> schema = new GremlinSchema<V>(clazz);
        schema.setClassName(className);
        schema.setClassType(clazz);
        schema.setSchemaType(getSchemaType(clazz));
        schema.setIdAccessor(idAccessor);
        schema.setIdEncoder(idEncoder);

        // Generate the Schema for clazz with all of it's super classes.
        populate(clazz, schema);
        if (schema.isWritable() && schema.getIdAccessor() == null) {
            throw new SchemaGeneratorException("Could not generate Schema for " + clazz.getSimpleName() + ". No @Id field found.");
        }
        return schema;
    }

    protected <S> GremlinSchema.SCHEMA_TYPE getSchemaType(Class<S> clazz) {
        if (entities.contains(clazz)) {
            return GremlinSchema.SCHEMA_TYPE.ENTITY;
        } else if (embedded.contains(clazz)) {
            return GremlinSchema.SCHEMA_TYPE.EMBEDDED;
        } else {
            return GremlinSchema.SCHEMA_TYPE.ENTITY;
        }
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
                if (isLinkField(cls, field)) {
                    property = propertyFactory.getLinkedProperty(cls, name);
                } else if (isCollectionField(cls, field)) {
                    cls = getCollectionType(field);
                    property = propertyFactory.getCollectiondProperty(cls, name);
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
        return embedded.contains(cls);
    }

    protected boolean isLinkField(Class<?> cls, Field field) {
        return entities.contains(cls);
    }

    protected boolean isCollectionField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && entities.contains(getCollectionType(field));
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
    protected boolean isLinkClass(Class<?> cls) {
        if (entities == null) {
            LOGGER.warn("Entities is null, this is unusual and is possibly an error. Please add the entity classes to the concrete SchemaBuilder.");
            return false;
        }

        return entities.contains(cls);
    }

    /**
     * @param cls
     * @return
     */
    protected boolean isEmbeddedClass(Class<?> cls) {
        if (embedded == null) {
            return false;
        }

        return embedded.contains(cls);
    }

    protected boolean acceptType(Class<?> cls) {
        return cls == Enum.class || ClassUtils.isPrimitiveOrWrapper(cls) || cls == String.class || Collection.class.isAssignableFrom(cls) || cls == Date.class || entities.contains(cls) || embedded.contains(cls);
    }

    @Override
    public void setEntities(Set<Class<?>> entities) {
        this.entities = entities;
    }

    @Override
    public void setEntities(Class<?>... entites) {
        setEntities(new HashSet<Class<?>>(Arrays.asList(entites)));
    }

    @Override
    public void setEmbedded(Set<Class<?>> embedded) {
        this.embedded = embedded;
    }

    @Override
    public void setEmbedded(Class<?>... embedded) {
        setEmbedded(new HashSet<Class<?>>(Arrays.asList(embedded)));
    }
}
