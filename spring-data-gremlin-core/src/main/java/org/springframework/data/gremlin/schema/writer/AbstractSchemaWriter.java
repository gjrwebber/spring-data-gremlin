package org.springframework.data.gremlin.schema.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinCollectionProperty;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

/**
 * An abstract {@link SchemaWriter} for implementing databases to extend for easy integration.
 *
 * @author Gman
 */
public abstract class AbstractSchemaWriter implements SchemaWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSchemaWriter.class);

    public enum CARDINALITY {
        ONE_TO_ONE,
        ONE_TO_MANY
    }

    @Override
    public void writeSchema(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {

        try {
            LOGGER.info("CREATING CLASS: " + schema.getClassName());
            Object vertex = createVertexClass(schema);
            LOGGER.info("CREATED CLASS: " + schema.getClassName());

            writeProperties(vertex, schema);

        } catch (Exception e) {

            rollback(schema);

            String msg = String.format("Could not create schema %s. ERROR: %s", schema, e.getMessage());
            LOGGER.error(e.getMessage(), e);
            throw new SchemaWriterException(msg, e);
        }
    }

    private void writeProperties(Object vertexClass, GremlinSchema<?> schema) {
        GremlinProperty latitude = null;
        GremlinProperty longitude = null;
        for (GremlinProperty property : schema.getProperties()) {

            Class<?> cls = property.getType();

            try {

                // If prop is null, it does not exist, so let's create it
                if (!isPropertyAvailable(vertexClass, property.getName())) {

                    // If this property is a LINK
                    if (property instanceof GremlinLinkProperty) {

                        createEdge((GremlinRelatedProperty) property, vertexClass, CARDINALITY.ONE_TO_ONE);
                        break;

                    } else if (property instanceof GremlinCollectionProperty) {
                        createEdge((GremlinRelatedProperty) property, vertexClass, CARDINALITY.ONE_TO_MANY);
                        break;

                    } else {
                        // Standard property, primitive, String, Enum, byte[]
                        Object prop = createProperty(vertexClass, property.getName(), cls);

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
            } catch (Exception e1) {
                LOGGER.warn(String.format("Could not create property %s of type %s", property, cls), e1);
            }
        }

        if (latitude != null && longitude != null) {
            createSpatialIndex(schema, latitude, longitude);
        }
    }


    private Object createEdge(GremlinRelatedProperty property, Object fromVertex, CARDINALITY cardinality) throws Exception {

        Object toVertex = createVertexClass(property.getRelatedSchema());
        Object edgeClass = createEdgeClass(property.getName(), fromVertex, toVertex, cardinality);

        return edgeClass;
    }

    protected abstract boolean isPropertyAvailable(Object vertexClass, String name);

    protected abstract Object createVertexClass(GremlinSchema schema) throws Exception;

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
