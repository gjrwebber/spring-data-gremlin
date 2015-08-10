package org.springframework.data.gremlin.schema;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinIdFieldPropertyAccessor;
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

    //    public enum SCHEMA_TYPE {
    //        ENTITY,
    //        EMBEDDED
    //    }

    public GremlinSchema(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinSchema() {
        classType = (Class<V>) GenericsUtil.getGenericType(this.getClass());
    }

    private String className;
    private Class<V> classType;
    //    private SCHEMA_TYPE schemaType;
    private boolean writable;
    private GremlinRepository<V> repository;
    private GremlinGraphFactory graphFactory;
    private GremlinIdFieldPropertyAccessor idAccessor;
    private GremlinPropertyMapper idMapper;
    private GremlinPropertyEncoder idEncoder;

    private Map<String, GremlinProperty> propertyMap = new HashMap<String, GremlinProperty>();
    private Map<String, GremlinProperty> fieldToPropertyMap = new HashMap<String, GremlinProperty>();
    private Multimap<Class<?>, GremlinProperty> typePropertyMap = LinkedListMultimap.create();

    private Set<GremlinProperty> properties = new HashSet<GremlinProperty>();

    public void addProperty(GremlinProperty property) {
        properties.add(property);
        propertyMap.put(property.getName(), property);
        fieldToPropertyMap.put(property.getAccessor().getField().getName(), property);
        typePropertyMap.put(property.getType(), property);
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

    public GremlinIdFieldPropertyAccessor getIdAccessor() {
        return idAccessor;
    }

    public void setIdAccessor(GremlinIdFieldPropertyAccessor idAccessor) {
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

    public Collection<GremlinProperty> getPropertyForType(Class<?> type) {
        return typePropertyMap.get(type);
    }
    //    public SCHEMA_TYPE getSchemaType() {
    //        return schemaType;
    //    }

    //    public void setSchemaType(SCHEMA_TYPE schemaType) {
    //        this.schemaType = schemaType;
    //    }

    //    public boolean isWritable() { return schemaType == GremlinSchema.SCHEMA_TYPE.ENTITY; }
    public boolean isWritable() { return writable; }


    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj) {
        cascadeCopyToGraph(graphAdapter, element, obj, new HashMap<Object, Object>());
    }

    public void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Map<Object, Object> cascadingSchemas, GremlinSchema cascadingFromSchema) {
        //        cascadingSchemas.add(cascadingFromSchema);
        cascadeCopyToGraph(graphAdapter, element, obj, cascadingSchemas);

    }

    private void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Map<Object, Object> cascadingSchemas) {

        if (cascadingSchemas.containsKey(obj)) {
            return;
        }
        cascadingSchemas.put(obj, element);


        for (GremlinProperty property : getProperties()) {

            //            if (ifAlreadyCascaded(property, cascadingSchemas)) {
            //                continue;
            //            }

            try {

                GremlinPropertyAccessor accessor = property.getAccessor();
                Object val = accessor.get(obj);

                if (val != null) {
                    property.copyToVertex(graphAdapter, element, val, cascadingSchemas);
                }
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()), e);
            }
        }
    }

    public V loadFromGraph(Element element) {

        //        V obj;
        //        try {
        //            obj = getClassType().newInstance();
        //            GremlinPropertyAccessor idAccessor = getIdAccessor();
        //            idAccessor.set(obj, encodeId(element.getId().toString()));
        //        } catch (Exception e) {
        //            throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
        //        }
        //        for (GremlinProperty property : getProperties()) {
        //            if (property instanceof GremlinRelatedProperty) {
        //                continue;
        //            }
        //            Object val = property.loadFromVertex(element, null);
        //
        //            try {
        //                GremlinPropertyAccessor accessor = property.getAccessor();
        //                accessor.set(obj, val);
        //            } catch (Exception e) {
        //                LOGGER.warn(String.format("Could not load property %s of %s", property, obj.toString()));
        //            }
        //        }
        //
        //        return obj;

        return cascadeLoadFromGraph(element, new HashMap<Object, Object>());
    }
    //
    //    public V cascadeLoadFromGraph(Element element, Map<GremlinSchema, V> cascadingSchemas, GremlinSchema cascadingFromSchema) {
    //        V v = cascadeLoadFromGraph(element, cascadingSchemas);
    //        return v;
    //    }

    public V cascadeLoadFromGraph(Element element, Map<Object, Object> cascadingSchemas) {

        V obj = (V) cascadingSchemas.get(element.getId());
        if (obj == null) {
            try {
                obj = getClassType().newInstance();
                //            cascadingSchemas.put(this, obj);

                GremlinPropertyAccessor idAccessor = getIdAccessor();
                idAccessor.set(obj, encodeId(element.getId().toString()));
                cascadingSchemas.put(element.getId(), obj);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
            }
            for (GremlinProperty property : getProperties()) {

                Object val = property.loadFromVertex(element, cascadingSchemas);
                //                cascadingSchemas.put(property, val);
                //            }

                try {
                    GremlinPropertyAccessor accessor = property.getAccessor();
                    accessor.set(obj, val);
                } catch (Exception e) {
                    LOGGER.warn(String.format("Could not load property %s of %s", property, obj.toString()));
                }
            }
        }
        return obj;
    }

    private boolean ifAlreadyCascaded(GremlinProperty property, Set<GremlinSchema> cascadingSchemas) {
        return property instanceof GremlinRelatedProperty && cascadingSchemas.contains(((GremlinRelatedProperty) property).getRelatedSchema());
    }

    private V getCascasedValue(GremlinProperty property, Map<GremlinSchema, V> cascadingSchemas) {
        if (property instanceof GremlinRelatedProperty) {
            return cascadingSchemas.get(((GremlinRelatedProperty) property).getRelatedSchema());
        }
        return null;
    }

    public String getGraphId(Object obj) {
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

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinSchema{");
        sb.append("className='").append(className).append('\'');
        sb.append(", classType=").append(classType);
        sb.append('}');
        return sb.toString();
    }
}
