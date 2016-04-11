package org.springframework.data.gremlin.schema.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Direction;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.schema.GremlinEdgeSchema;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinVertexSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinPropertyFactory;
import org.springframework.data.gremlin.schema.property.accessor.*;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.utils.GenericsUtil;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
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
public class BasicSchemaGenerator implements SchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSchemaGenerator.class);
    private ObjectMapper objectMapper;
    private Set<Class<?>> vertexClasses;
    private Set<Class<?>> embeddedClasses;
    private Set<Class<?>> edgeClasses;
    private GremlinPropertyFactory propertyFactory;
    private GremlinPropertyEncoder idEncoder;

    public BasicSchemaGenerator() {
        this(null, new GremlinPropertyFactory(), null);
    }

    public BasicSchemaGenerator(ObjectMapper objectMapper) {
        this(null, new GremlinPropertyFactory(), objectMapper);
    }

    public BasicSchemaGenerator(GremlinPropertyEncoder idEncoder) {
        this(idEncoder, new GremlinPropertyFactory(), null);
    }

    public BasicSchemaGenerator(GremlinPropertyEncoder idEncoder, ObjectMapper objectMapper) {
        this(idEncoder, new GremlinPropertyFactory(), objectMapper);
    }

    public BasicSchemaGenerator(GremlinPropertyEncoder idEncoder, GremlinPropertyFactory propertyFactory) {
        this(idEncoder, propertyFactory, null);
    }

    public BasicSchemaGenerator(GremlinPropertyEncoder idEncoder, GremlinPropertyFactory propertyFactory, ObjectMapper objectMapper) {
        this.idEncoder = idEncoder;
        this.propertyFactory = propertyFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * @param clazz The Class to create a GremlinSchema from
     * @return A GremlinSchema for the given Class
     */

    public <V> GremlinSchema<V> generateSchema(Class<V> clazz) throws SchemaGeneratorException {
        String className = getVertexName(clazz);

        Field field = getIdField(clazz);
        GremlinIdFieldPropertyAccessor idAccessor = new GremlinIdFieldPropertyAccessor(field);

        GremlinSchema<V> schema = createSchema(clazz);
        schema.setClassName(className);
        schema.setClassType(clazz);
        schema.setIdAccessor(idAccessor);
        schema.setIdEncoder(idEncoder);

        // Generate the Schema for clazz with all of it's super classes.
        populate(clazz, schema);
        if (schema.isVertexSchema() && schema.getIdAccessor() == null) {
            throw new SchemaGeneratorException("Could not generate Schema for " + clazz.getSimpleName() + ". No @Id field found.");
        }
        return schema;
    }

    public <V> GremlinSchema<V> generateDynamicSchema(String className, Map<String, Object> map) throws SchemaGeneratorException {

        GremlinIdMapPropertyAccessor idAccessor = new GremlinIdMapPropertyAccessor();
        GremlinVertexSchema<V> schema = new GremlinVertexSchema(Map.class);
        schema.setClassName(className);
        schema.setIdAccessor(idAccessor);
        schema.setIdEncoder(idEncoder);

        for (String key : map.keySet()) {
            Object val = map.get(key);
            GremlinProperty property = propertyFactory.getProperty(val.getClass(), key);
            property.setAccessor(new GremlinMapPropertyAccessor(key, val.getClass(), null));
            schema.addProperty(property);
        }

        return schema;
    }

    protected <S> boolean isSchemaWritable(Class<S> clazz) {
        return isVertexClass(clazz) || isEdgeClass(clazz);
    }

    protected <V, S> void populate(Class<V> clazz, GremlinSchema<S> schema) {
        populate(clazz, schema, null);
    }

    private <V> GremlinSchema<V> createSchema(Class<V> clazz) {
        if (isVertexClass(clazz)) {
            return new GremlinVertexSchema<>(clazz);
        } else if (isEdgeClass(clazz)) {
            return new GremlinEdgeSchema<>(clazz);
        } else {
            throw new IllegalArgumentException(clazz + " cannot be classes as a VERTEX or EDGE!");
        }
    }

    /**
     * @param clazz
     * @param schema
     * @param embeddedFieldAccessor If this method was called for an embedded Field it is provided here
     */
    protected <V, S> void populate(final Class<V> clazz, final GremlinSchema<S> schema, final GremlinFieldPropertyAccessor embeddedFieldAccessor) {

        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {BasicSchemaGenerator.this.processField(field, schema, objectMapper, embeddedFieldAccessor);}
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                boolean result = shouldProcessField(schema, field);
                return result;
            }
        });
    }

    protected <S> void processField(Field field, GremlinSchema<S> schema, ObjectMapper objectMapper, GremlinFieldPropertyAccessor embeddedFieldAccessor) {


        Class<?> cls = field.getType();

        GremlinPropertyAccessor accessor = new GremlinFieldPropertyAccessor(field, embeddedFieldAccessor);
        GremlinProperty property = null;

        // Get the rootEmbeddedField
        Field rootEmbeddedField = null;
        if (embeddedFieldAccessor != null) {
            rootEmbeddedField = embeddedFieldAccessor.getRootField();
        }
        String name = getPropertyName(field, rootEmbeddedField, schema.getClassType());

        // Check if we accept this standard type
        //        if (acceptType(cls)) {

        // If it is an enum, check if it is annotated with @Enumerated
        if (isEnumField(cls, field)) {
            Class<?> enumType = cls;

            cls = getEnumType(field);
            if (Collection.class.isAssignableFrom(field.getType())) {
                Class<Collection<Enum>> enumCollectionCls = getEnumCollectionType(field);
                if (enumCollectionCls.isInterface()) {
                    throw new IllegalArgumentException("Collection is an interface (" + enumCollectionCls +
                                                       "). The concrete type cannot be determined. Please use a concrete Collection type or use @Enumerated(collectionType=HashSet.class)");
                }
                boolean useOrdinal = cls == Integer.class;
                accessor = new GremlinEnumStringCollectionFieldPropertyAccessor(field, enumCollectionCls, useOrdinal);
                cls = String.class;
            } else {
                if (cls == String.class) {
                    accessor = new GremlinEnumStringFieldPropertyAccessor(field, enumType);
                } else if (cls == Integer.class) {
                    accessor = new GremlinEnumOrdinalFieldPropertyAccessor(field, enumType);
                } else {
                    accessor = new GremlinEnumOrdinalFieldPropertyAccessor(field, enumType);
                }
            }
        } else if (isCollectionViaField(cls, field)) {
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
                property = propertyFactory.getAdjacentProperty(cls, "out", Direction.OUT);
            } else {
                property = propertyFactory.getAdjacentProperty(cls, "in", Direction.IN);
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
//        } else if (isDynamicVertex(cls, field)) {
//            GremlinSchema dynamicSchema = generateDynamicSchema(name, field.g)
//            if (isLinkOutward(cls, field)) {
//                property = propertyFactory.getLinkProperty(cls, name, Direction.OUT);
//            } else {
//                property = propertyFactory.getLinkProperty(cls, name, Direction.IN);
//            }
        } else if (isSerialisableField(cls, field)) {
            accessor = new GremlinSerializableFieldPropertyAccessor(field, embeddedFieldAccessor);
            cls = getSerializableType(field);
        } else if (isJsonField(cls, field)) {
            Class<?> mixin = getJsonMixin(field);
            accessor = new GremlinJSONFieldPropertyAccessor(field, mixin, embeddedFieldAccessor, objectMapper);
            cls = getJsonType(field);
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
        //        }
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
        return field != null
               //               && acceptType(field.getType())
               && schema.getIdAccessor() instanceof GremlinFieldPropertyAccessor && !((GremlinFieldPropertyAccessor) schema.getIdAccessor()).getField().equals(field) && !Modifier.isTransient(
                field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
    }

    protected Field getIdField(Class<?> cls) throws SchemaGeneratorException {
        try {
            Field field = ReflectionUtils.findField(cls, "id");
            if (field != null && (field.getType() == Long.class || field.getType() == String.class)) {
                return field;
            } else {
                throw new NoSuchFieldException("");
            }
        } catch (NoSuchFieldException e) {
            throw new SchemaGeneratorException("Cannot generate schema as there is no ID field. You must have a field of type Long or String named 'id'.");
        }
    }

    protected Class<Collection<Enum>> getEnumCollectionType(Field field) {
        return (Class<Collection<Enum>>) field.getType();
    }

    protected Class<?> getEnumType(Field field) {
        return String.class;
    }

    private Class<?> getSerializableType(Field field) {
        return byte[].class;
    }

    private Class<?> getJsonType(Field field) {
        return String.class;
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

    protected String getPropertyName(Field field, Field rootEmbeddedField, Class<?> schemaClass) {
        String propertyName = field.getName();

        if (rootEmbeddedField != null) {
            propertyName = String.format("%s_%s", getPropertyName(rootEmbeddedField, null, schemaClass), propertyName);
        }
        if (field.getDeclaringClass() != schemaClass) {
            propertyName = String.format("%s_%s", schemaClass.getSimpleName().toLowerCase(), propertyName);
        }
        return propertyName;
    }

    protected boolean isSerialisableField(Class<?> cls, Field field) {
        return !stdType(cls) && (Serializable.class.isAssignableFrom(cls) || (Collection.class.isAssignableFrom(cls) && Serializable.class.isAssignableFrom(getCollectionType(field))));
    }

    protected boolean isJsonField(Class<?> cls, Field field) {
        return !stdType(cls);
    }

    protected Class<?> getJsonMixin(Field field) {
        return null;
    }

    protected boolean isEnumField(Class<?> cls, Field field) {
        return cls.isEnum() || (Collection.class.isAssignableFrom(cls) && getCollectionType(field).isEnum());
    }

    protected boolean isEmbeddedField(Class<?> cls, Field field) {
        return isEmbeddedClass(cls);
    }

    protected boolean isDynamicVertex(Class<?> cls, Field field) {
        return Map.class.isAssignableFrom(cls) && GenericsUtil.getGenericTypes(field, 2)[1] == String.class;
    }

    protected boolean isLinkField(Class<?> cls, Field field) {
        return isVertexClass(cls);
    }

    protected boolean isLinkViaField(Class<?> cls, Field field) {
        return isEdgeClass(cls);
    }

    protected boolean isAdjacentField(Class<?> cls, Field field) {
        return false;
    }

    protected boolean isAdjacentOutward(Class<?> cls, Field field) {
        return false;
    }

    protected boolean isLinkOutward(Class<?> cls, Field field) {
        return true;
    }

    protected boolean isCollectionField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isVertexClass(getCollectionType(field));
    }

    protected boolean isCollectionViaField(Class<?> cls, Field field) {
        return Collection.class.isAssignableFrom(cls) && isEdgeClass(getCollectionType(field));
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
    protected boolean isVertexClass(Class<?> cls) {
        if (vertexClasses == null) {
            LOGGER.warn("Entities is null, this is unusual and is possibly an error. Please add the entity classes to the concrete SchemaBuilder.");
            return false;
        }

        return vertexClasses.contains(cls);
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
    protected boolean isEdgeClass(Class<?> cls) {
        if (edgeClasses == null) {
            return false;
        }

        return edgeClasses.contains(cls);
    }

    protected boolean acceptType(Class<?> cls) {
        return Enum.class.isAssignableFrom(cls) ||
               ClassUtils.isPrimitiveOrWrapper(cls) ||
               cls == String.class ||
               Collection.class.isAssignableFrom(cls) ||
               cls == Date.class ||
               isVertexClass(cls) ||
               isEmbeddedClass(cls) ||
               isEdgeClass(cls);
    }

    protected boolean stdType(Class<?> cls) {
        return ClassUtils.isPrimitiveOrWrapper(cls) ||
               cls == String.class ||
               cls == Date.class;
    }

    @Override
    public void setVertexClasses(Set<Class<?>> entityClasses) {
        this.vertexClasses = entityClasses;
    }

    @Override
    public void setVertexClasses(Class<?>... entites) {
        setVertexClasses(new HashSet<Class<?>>(Arrays.asList(entites)));
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
    public void setEdgeClasses(Set<Class<?>> relationshipClasses) {
        this.edgeClasses = relationshipClasses;
    }

    @Override
    public void setEdgeClasses(Class<?>... relationshipClasses) {
        setEdgeClasses(new HashSet<Class<?>>(Arrays.asList(relationshipClasses)));
    }


}
