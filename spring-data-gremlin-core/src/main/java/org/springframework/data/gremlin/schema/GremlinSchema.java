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

    public GremlinSchema(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinSchema() {
        classType = (Class<V>) GenericsUtil.getGenericType(this.getClass());
    }

    private String className;
    private Class<V> classType;
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

    public boolean isWritable() { return writable; }


    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj) {
        cascadeCopyToGraph(graphAdapter, element, obj, new HashMap<Object, Element>());
    }

    public void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Map<Object, Element> cascadingSchemas) {

        if (cascadingSchemas.containsKey(obj)) {
            return;
        }
        cascadingSchemas.put(obj, element);

        for (GremlinProperty property : getProperties()) {

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

        return cascadeLoadFromGraph(element, new HashMap<>());
    }

    public V cascadeLoadFromGraph(Element element, Map<Object, Object> cascadingSchemas) {

        V obj = (V) cascadingSchemas.get(element.getId());
        if (obj == null) {
            try {
                obj = getClassType().newInstance();

                GremlinPropertyAccessor idAccessor = getIdAccessor();
                idAccessor.set(obj, encodeId(element.getId().toString()));
                cascadingSchemas.put(element.getId(), obj);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
            }
            for (GremlinProperty property : getProperties()) {

                Object val = property.loadFromVertex(element, cascadingSchemas);

                GremlinPropertyAccessor accessor = property.getAccessor();
                try {
                    accessor.set(obj, val);
                } catch (Exception e) {
                    LOGGER.warn(String.format("Could not load property %s of %s", property, obj.toString()), e);
                }
            }
        }
        return obj;
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
