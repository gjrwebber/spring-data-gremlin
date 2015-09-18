package org.springframework.data.gremlin.schema.writer;

import com.tinkerpop.blueprints.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

import static org.springframework.data.gremlin.schema.property.GremlinRelatedProperty.CARDINALITY;

/**
 * An abstract {@link SchemaWriter} for implementing databases to extend for easy integration.
 *
 * @author Gman
 */
public abstract class AbstractSchemaWriter implements SchemaWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSchemaWriter.class);

    @Override
    public void writeSchema(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CREATING CLASS: " + schema.getClassName());
            }
            Object element;
            if (schema.isVertexSchema()) {
                element = createVertexClass(schema);
            } else if (schema.isEdgeSchema()) {

                element = createEdgeClass(schema.getClassName(), schema.getOutProperty().getRelatedSchema().getClassType(),)
            } else {
                throw new IllegalStateException("Unknown class type. Expected Vertex or Edge.");
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CREATED CLASS: " + schema.getClassName());
            }


            writeProperties(element, schema);

        } catch (Exception e) {

            rollback(schema);

            String msg = String.format("Could not create schema %s. ERROR: %s", schema, e.getMessage());
            LOGGER.error(e.getMessage(), e);
            throw new SchemaWriterException(msg, e);
        }
    }

    private void writeProperties(Object elementClass, GremlinSchema<?> schema) {
        GremlinProperty latitude = null;
        GremlinProperty longitude = null;
        for (GremlinProperty property : schema.getProperties()) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CREATING Property: " + property.getName());
            }
            Class<?> cls = property.getType();

            try {

                // If prop is null, it does not exist, so let's create it
                if (!isPropertyAvailable(elementClass, property.getName())) {

                    if (property instanceof GremlinAdjacentProperty) {

                        GremlinAdjacentProperty adjacentProperty = (GremlinAdjacentProperty) property;

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("CREATING RELATED PROPERTY: " + schema.getClassName() + "." + property.getName());
                        }
                        Object relatedVertex = createVertexClass(adjacentProperty.getRelatedSchema());

                        if (((GremlinRelatedProperty) property).getDirection() == Direction.OUT) {
                            createEdgeClass(property.getName(), elementClass, relatedVertex, relatedProperty.getCardinality());
                        } else {
                            createEdgeClass(property.getName(), relatedVertex, elementClass, relatedProperty.getCardinality());
                        }
                    }

                    if (property instanceof GremlinRelatedProperty) {

                        GremlinRelatedProperty relatedProperty = (GremlinRelatedProperty) property;
                        if (relatedProperty.getRelatedSchema().isVertexSchema()) {

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("CREATING RELATED PROPERTY: " + schema.getClassName() + "." + property.getName());
                            }
                            Object relatedVertex = createVertexClass(relatedProperty.getRelatedSchema());

                            if (((GremlinRelatedProperty) property).getDirection() == Direction.OUT) {
                                createEdgeClass(property.getName(), elementClass, relatedVertex, relatedProperty.getCardinality());
                            } else {
                                createEdgeClass(property.getName(), relatedVertex, elementClass, relatedProperty.getCardinality());
                            }
                        } else {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("CREATING RELATED EDGE: " + schema.getClassName() + "." + property.getName());
                            }
                            Object relatedVertex = createVertexClass(relatedProperty.getAdjacentProperty().getRelatedSchema());

                            if (((GremlinRelatedProperty) property).getDirection() == Direction.OUT) {
                                createEdgeClass(relatedProperty.getRelatedSchema().getClassName(), elementClass, relatedVertex, relatedProperty.getCardinality());
                            } else {
                                createEdgeClass(relatedProperty.getRelatedSchema().getClassName(), relatedVertex, elementClass, relatedProperty.getCardinality());
                            }
                        }

                    } else {

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("CREATING PROPERTY: " + schema.getClassName() + "." + property.getName());
                        }
                        // Standard property, primitive, String, Enum, byte[]
                        Object prop = createProperty(elementClass, property.getName(), cls);

                        switch (property.getIndex()) {
                        case UNIQUE:
                            createUniqueIndex(prop);
                            break;
                        case NON_UNIQUE:
                            createNonUniqueIndex(prop);
                            break;
                        case SPATIAL_LATITUDE:
                            latitude = property;
                            break;

                        case SPATIAL_LONGITUDE:
                            longitude = property;
                            break;
                        }
                    }
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("CREATED CLASS: " + schema.getClassName());
                }
            } catch (Exception e1) {
                LOGGER.warn(String.format("Could not create property %s of type %s", property, cls), e1);
            }
        }

        if (latitude != null && longitude != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CREATING SPATIAL INDEX...");
            }
            createSpatialIndex(schema, latitude, longitude);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CREATED SPATIAL INDEX.");
            }
        }
    }


    protected abstract boolean isPropertyAvailable(Object vertexClass, String name);

    protected abstract Object createVertexClass(GremlinSchema schema) throws Exception;

    protected abstract Object createEdgeClass(GremlinSchema schema) throws Exception;

    protected abstract void rollback(GremlinSchema schema);

    protected abstract Object createEdgeClass(String name, Object outVertex, Object inVertex, CARDINALITY cardinality) throws SchemaWriterException;

    protected abstract boolean isEdgeInProperty(Object edgeClass);

    protected abstract boolean isEdgeOutProperty(Object edgeClass);

    protected abstract Object setEdgeOut(Object edgeClass, Object vertexClass);

    protected abstract Object setEdgeIn(Object edgeClass, Object vertexClass);

    protected abstract Object createProperty(Object parentElement, String name, Class<?> cls);

    protected abstract void createNonUniqueIndex(Object prop);

    protected abstract void createUniqueIndex(Object prop);

    protected abstract void createSpatialIndex(GremlinSchema<?> schema, GremlinProperty latitude, GremlinProperty longitude);

}
