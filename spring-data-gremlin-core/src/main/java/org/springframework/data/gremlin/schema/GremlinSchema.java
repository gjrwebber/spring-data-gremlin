package org.springframework.data.gremlin.schema;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyProperty;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyVertexProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinIdPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.schema.property.mapper.GremlinPropertyMapper;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.utils.GenericsUtil;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (VERTEX, EDGE) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Gman
 */
public abstract class GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinSchema.class);

    public GremlinSchema(Class<V> classType) {
        this.classType = classType;
    }

    public GremlinSchema() {
        classType = (Class<V>) GenericsUtil.getGenericType(this.getClass());
    }

    private String className;
    private Class<V> classType;
    private GremlinRepository<V> repository;
    private GremlinGraphFactory graphFactory;
    private GremlinIdPropertyAccessor idAccessor;
    private GremlinPropertyMapper idMapper;
    private GremlinPropertyEncoder idEncoder;

    private GremlinAdjacentProperty outProperty;
    private GremlinAdjacentProperty inProperty;

    private Map<String, GremlinProperty> propertyMap = new HashMap<String, GremlinProperty>();
    private Map<String, GremlinProperty> fieldToPropertyMap = new HashMap<String, GremlinProperty>();
    private Multimap<Class<?>, GremlinProperty> typePropertyMap = LinkedListMultimap.create();

    private Set<GremlinProperty> properties = new HashSet<GremlinProperty>();

    public void addProperty(GremlinProperty property) {
        property.setSchema(this);
        if (property instanceof GremlinAdjacentProperty) {
            if (((GremlinAdjacentProperty) property).getDirection() == Direction.OUT) {
                outProperty = (GremlinAdjacentProperty) property;
            } else {
                inProperty = (GremlinAdjacentProperty) property;
            }
        }
        properties.add(property);
        propertyMap.put(property.getName(), property);
        if (property.getAccessor() instanceof GremlinFieldPropertyAccessor) {
            fieldToPropertyMap.put(((GremlinFieldPropertyAccessor) property.getAccessor()).getField().getName(), property);
        }
        typePropertyMap.put(property.getType(), property);
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

    public GremlinIdPropertyAccessor getIdAccessor() {
        return idAccessor;
    }

    public void setIdAccessor(GremlinIdPropertyAccessor idAccessor) {
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

    public boolean isVertexSchema() {
        return this instanceof GremlinVertexSchema;
    }

    public boolean isEdgeSchema() {
        return this instanceof GremlinEdgeSchema;
    }

    public GremlinAdjacentProperty getOutProperty() {
        return outProperty;
    }

    public GremlinAdjacentProperty getInProperty() {
        return inProperty;
    }

    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj, Object... noCascade) {
        Map<Object, Element> noCascadingMap = new HashMap<>();
        for (Object skip : noCascade) {
            noCascadingMap.put(skip, element);
        }
        cascadeCopyToGraph(graphAdapter, element, obj, noCascadingMap);
    }

    public void copyToGraph(GremlinGraphAdapter graphAdapter, Element element, Object obj) {
        cascadeCopyToGraph(graphAdapter, element, obj, new HashMap<Object, Element>());
    }

    public void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, final Object obj, Map<Object, Element> noCascadingMap) {

        if (noCascadingMap.containsKey(obj)) {
            return;
        }
        noCascadingMap.put(obj, element);

        for (GremlinProperty property : getProperties()) {

            try {

                GremlinPropertyAccessor accessor = property.getAccessor();
                Object val = accessor.get(obj);

                if (val != null) {
                    property.copyToVertex(graphAdapter, element, val, noCascadingMap);
                }
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Could not save property %s of %s", property, obj.toString()), e);
            }
        }

        if (getGraphId(obj) == null && TransactionSynchronizationManager.isSynchronizationActive()) {
            final Element finalElement = element;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    setObjectId(obj, finalElement);
                }
            });
        }
    }

    public V loadFromGraph(GremlinGraphAdapter graphAdapter, Element element) {

        return cascadeLoadFromGraph(graphAdapter, element, new HashMap<>());
    }

    public V cascadeLoadFromGraph(GremlinGraphAdapter graphAdapter, Element element, Map<Object, Object> noCascadingMap) {

        V obj = (V) noCascadingMap.get(element.id());
        if (obj == null) {
            try {
                obj = getClassType().newInstance();

                GremlinPropertyAccessor idAccessor = getIdAccessor();
                idAccessor.set(obj, encodeId(element.id().toString()));
                noCascadingMap.put(element.id(), obj);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate new " + getClassType(), e);
            }
            for (GremlinProperty property : getProperties()) {
                Object val = property.loadFromVertex(graphAdapter, element, noCascadingMap);

                GremlinPropertyAccessor accessor = property.getAccessor();
                if (val instanceof EmptyVertexProperty || val instanceof EmptyProperty) {
                    continue;
                }

                if (val instanceof Property) {
                    Property prop = ((Property)val);
                    if (prop.isPresent()) {
                        val = ((Property) val).value();
                    }
                }

                if (val instanceof Edge || val instanceof Vertex) {
                    continue;
                }



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

    public void setObjectId(Object obj, Element element) {
        getIdAccessor().set(obj, encodeId(element.id().toString()));
    }

    public String getObjectId(Object obj) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinSchema{");
        sb.append("className='").append(className).append('\'');
        sb.append(", classType=").append(classType);
        sb.append('}');
        return sb.toString();
    }
}
