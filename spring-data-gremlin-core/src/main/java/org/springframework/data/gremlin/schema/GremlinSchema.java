package org.springframework.data.gremlin.schema;

import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.schema.property.mapper.GremlinPropertyMapper;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.utils.GenericsUtil;

import java.util.*;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (ENTITY, EMBEDDED) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Gman
 */
public class GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinSchema.class);

    public enum SCHEMA_TYPE {
        ENTITY,
        EMBEDDED
    }

    public GremlinSchema(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinSchema() {
        classType = (Class<V>) GenericsUtil.getGenericType(this.getClass());
    }

    private String className;
    private Class<V> classType;
    private SCHEMA_TYPE schemaType;
    private GremlinRepository<V> repository;
    private GremlinGraphFactory graphFactory;
    private GremlinFieldPropertyAccessor<String> idAccessor;
    private GremlinPropertyMapper idMapper;
    private GremlinPropertyEncoder idEncoder;

    private Map<String, GremlinProperty> propertyMap = new HashMap<String, GremlinProperty>();
    private Map<String, GremlinProperty> fieldToPropertyMap = new HashMap<String, GremlinProperty>();

    private Set<GremlinProperty> properties = new HashSet<GremlinProperty>();

    public void addProperty(GremlinProperty property) {
        properties.add(property);
        propertyMap.put(property.getName(), property);
        fieldToPropertyMap.put(property.getAccessor().getField().getName(), property);
        property.setSchema(this);
    }

    public GremlinProperty getPropertyForFieldname(String fieldname) {
        return fieldToPropertyMap.get(fieldname);
    }

    public GremlinPropertyMapper getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(GremlinPropertyMapper idMapper) {
        this.idMapper = idMapper;
    }

    public GremlinGraphFactory getGraphFactory() {
        return graphFactory;
    }

    public void setGraphFactory(GremlinGraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }

    public GremlinRepository<V> getRepository() {
        return repository;
    }

    public void setRepository(GremlinRepository<V> repository) {
        this.repository = repository;
    }

    public GremlinPropertyEncoder getIdEncoder() {
        return idEncoder;
    }

    public void setIdEncoder(GremlinPropertyEncoder idEncoder) {
        this.idEncoder = idEncoder;
    }

    public GremlinFieldPropertyAccessor<String> getIdAccessor() {
        return idAccessor;
    }

    public void setIdAccessor(GremlinFieldPropertyAccessor<String> idAccessor) {
        this.idAccessor = idAccessor;
    }

    public Collection<String> getPropertyNames() {
        return propertyMap.keySet();
    }

    public GremlinPropertyAccessor getAccessor(String property) {
        return propertyMap.get(property).getAccessor();
    }

    public Collection<GremlinProperty> getProperties() {
        return properties;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class<V> getClassType() {
        return classType;
    }

    public void setClassType(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinProperty getProperty(String property) {
        return propertyMap.get(property);
    }

    public SCHEMA_TYPE getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(SCHEMA_TYPE schemaType) {
        this.schemaType = schemaType;
    }

    public boolean isWritable() { return schemaType == GremlinSchema.SCHEMA_TYPE.ENTITY;}


    public void copyToVertex(GremlinGraphAdapter graphAdapter, Vertex vertex, Object obj) {
        cascadeCopyToVertex(graphAdapter, vertex, obj, new HashSet<GremlinSchema>());
    }

    public void cascadeCopyToVertex(GremlinGraphAdapter graphAdapter, Vertex vertex, Object obj, Set<GremlinSchema> cascadingSchemas, GremlinSchema cascadingFromSchema) {
        cascadingSchemas.add(cascadingFromSchema);
        cascadeCopyToVertex(graphAdapter, vertex, obj, cascadingSchemas);

    }

    private void cascadeCopyToVertex(GremlinGraphAdapter graphAdapter, Vertex vertex, Object obj, Set<GremlinSchema> cascadingSchemas) {

        for (GremlinProperty property : getProperties()) {

            if (ifAlreadyCascaded(property, cascadingSchemas)) {
                continue;
            }

            try {

                GremlinPropertyAccessor accessor = property.getAccessor();
                Object val = accessor.get(obj);

                if (val != null) {
                    property.copyToVertex(graphAdapter, vertex, val, cascadingSchemas);
                }
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()), e);
            }
        }
    }

    public V loadFromVertex(Vertex vertex) {
        return cascadeLoadFromVertex(vertex, new HashSet<GremlinSchema>());
    }

    public V cascadeLoadFromVertex(Vertex vertex, Set<GremlinSchema> cascadingSchemas, GremlinSchema cascadingFromSchema) {
        cascadingSchemas.add(cascadingFromSchema);
        return cascadeLoadFromVertex(vertex, cascadingSchemas);
    }

    public V cascadeLoadFromVertex(Vertex vertex, Set<GremlinSchema> cascadingSchemas) {

        V obj;
        try {
            obj = getClassType().newInstance();

            GremlinPropertyAccessor idAccessor = getIdAccessor();
            idAccessor.set(obj, encodeId(vertex.getId().toString()));
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
        }
        for (GremlinProperty property : getProperties()) {

            if (ifAlreadyCascaded(property, cascadingSchemas)) {
                continue;
            }

            try {
                Object val = property.loadFromVertex(vertex, cascadingSchemas);
                GremlinPropertyAccessor accessor = property.getAccessor();
                accessor.set(obj, val);
            } catch (Exception e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()));
            }
        }
        return obj;
    }

    private boolean ifAlreadyCascaded(GremlinProperty property, Set<GremlinSchema> cascadingSchemas) {
        return property instanceof GremlinRelatedProperty && cascadingSchemas.contains(((GremlinRelatedProperty) property).getRelatedSchema());
    }

    public String getVertexId(Object obj) {
        return decodeId(getIdAccessor().get(obj));
    }

    public void setObjectId(V obj, Vertex vertex) {
        getIdAccessor().set(obj, encodeId(vertex.getId().toString()));
    }

    public String getObjectId(V obj) {
        String id = getIdAccessor().get(obj);
        if (id != null) {
            return decodeId(id);
        }
        return null;
    }

    public String encodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.encode(id).toString();
        }
        return id;
    }

    public String decodeId(String id) {
        if (id == null) {
            return null;
        }
        if (idEncoder != null) {
            id = idEncoder.decode(id).toString();
        }
        return id;
    }

}
