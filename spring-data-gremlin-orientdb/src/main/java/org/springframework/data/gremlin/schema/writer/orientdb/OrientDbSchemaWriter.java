package org.springframework.data.gremlin.schema.writer.orientdb;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.writer.AbstractSchemaWriter;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.schema.writer.SchemaWriterException;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.orientdb.OrientDBGremlinGraphFactory;

import static org.springframework.data.gremlin.schema.property.GremlinRelatedProperty.CARDINALITY;

/**
 * A concrete {@link SchemaWriter} for an OrientDB database.
 *
 * @author Gman
 */
public class OrientDbSchemaWriter extends AbstractSchemaWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDbSchemaWriter.class);

    private OrientDBGremlinGraphFactory dbf;
    private OSchema oSchema;
    private OClass v;
    private OClass e;
    OrientGraphNoTx graph = null;

    public void initialise(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {

        LOGGER.debug("Initialising...");

        try {
            dbf = (OrientDBGremlinGraphFactory) tgf;
            graph = dbf.graphNoTx();
            oSchema = graph.getRawGraph().getMetadata().getSchema();

        } catch (RuntimeException e) {
            String msg = String.format("Could not create schema %s. ERROR: %s", schema, e.getMessage());
            throw new SchemaWriterException(msg, e);
        }
        try {

            v = graph.getVertexBaseType();
            e = graph.getEdgeBaseType();

        } catch (Exception e) {

            // If any exception, drop the class
            try {
                oSchema.dropClass(schema.getClassName());
            } catch (Exception e1) {
                // Ignore
            }

            String msg = String.format("Could not create schema %s. ERROR: %s", schema, e.getMessage());
            LOGGER.error(e.getMessage(), e);
            throw new SchemaWriterException(msg, e);
        }



        LOGGER.debug("Initialised.");
    }

    @Override
    public void writeSchema(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {
        initialise(tgf, schema);
        super.writeSchema(tgf, schema);
        //graph.shutdown();
    }

    @Override
    protected boolean isPropertyAvailable(Object vertexClass, String name) {
        OProperty prop = ((OClass) vertexClass).getProperty(name);
        return prop != null;
    }

    @Override
    protected Object createVertexClass(GremlinSchema schema) throws Exception {
        OClass vClass = getOrCreateClass(oSchema, v, schema.getClassName());
        return vClass;
    }

    @Override
    protected Object createEdgeClass(GremlinSchema schema) throws Exception {
        OClass edgeClass = getOrCreateClass(oSchema, e, schema.getClassName());
        return edgeClass;
    }

    @Override
    protected void rollback(GremlinSchema schema) {

        // If any exception, drop the class
        try {
            oSchema.dropClass(schema.getClassName());
        } catch (Exception e1) {
            // Ignore
        }

    }

    @Override
    protected Object createEdgeClass(String name, Object outVertex, Object inVertex, CARDINALITY cardinality) throws SchemaWriterException {
        OClass edgeClass = getOrCreateClass(oSchema, e, name);

        if (!edgeClass.existsProperty("out")) {
            OProperty out = edgeClass.createProperty("out", OType.LINK);
            out.setLinkedClass((OClass) outVertex);

            out.setMax("1");
            if (cardinality == CARDINALITY.ONE_TO_MANY) {
                out.setMax(String.valueOf(Integer.MAX_VALUE));
            }
        }

        if (!edgeClass.existsProperty("in")) {
            OProperty in = edgeClass.createProperty("in", OType.LINK);
            in.setLinkedClass((OClass) inVertex);

            in.setMax("1");
            if (cardinality == CARDINALITY.MANY_TO_ONE) {
                in.setMax(String.valueOf(Integer.MAX_VALUE));
            }
        }

        return edgeClass;
    }

    @Override
    protected boolean isEdgeInProperty(Object edgeClass) {
        return ((OClass) edgeClass).getProperty("in") != null;
    }

    @Override
    protected boolean isEdgeOutProperty(Object edgeClass) {
        return ((OClass) edgeClass).getProperty("out") != null;
    }

    @Override
    protected Object setEdgeOut(Object edgeClass, Object vertexClass) {
        OProperty out = ((OClass) edgeClass).createProperty("out", OType.LINK);
        out.setLinkedClass((OClass) vertexClass);
        return out;
    }

    @Override
    protected Object setEdgeIn(Object edgeClass, Object vertexClass) {
        OProperty in = ((OClass) edgeClass).createProperty("in", OType.LINK);
        in.setLinkedClass((OClass) vertexClass);
        return in;
    }

    @Override
    protected Object createProperty(Object parentElement, String name, Class<?> cls) {
        OType oType = OType.getTypeByClass(cls);
        return ((OClass) parentElement).createProperty(name, oType);
    }

    @Override
    protected void createNonUniqueIndex(Object prop) {
        ((OProperty) prop).createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
    }

    @Override
    protected void createUniqueIndex(Object prop) {
        ((OProperty) prop).createIndex(OClass.INDEX_TYPE.UNIQUE);
    }

    @Override
    protected void createSpatialIndex(GremlinSchema<?> schema, GremlinProperty latitude, GremlinProperty longitude) {

        String indexName = schema.getClassName() + ".lat_lon";
        if (dbf.graphNoTx().getIndex(indexName, Vertex.class) == null) {
            try {
                dbf.graphNoTx().command(new OCommandSQL(String.format("CREATE INDEX %s ON %s(%s,%s) SPATIAL ENGINE LUCENE METADATA {ignoreNullValues: true}", indexName, schema.getClassName(), latitude.getName(), longitude.getName())))
                   .execute();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private OClass getOrCreateClass(OSchema oSchema, OClass superclass, String classname) throws SchemaWriterException {
        OClass newClass = oSchema.getOrCreateClass(classname, superclass);
        if (!newClass.getSuperClass().getName().equals(superclass.getName())) {
            String msg = String.format("Could not create %s '%s' of type %s. A conflicting %s exists of type %s", getClassType(superclass), classname, superclass.getName(), getClassType(superclass),
                                       newClass.getSuperClass().getName());
            throw new SchemaWriterException(msg);
        }
        return newClass;
    }

    private String getClassType(OClass type) {
        return (type.getName().equals("E")) ? "property" : "class";
    }
    //
    //    private void writeProperties(OrientDBGremlinGraphFactory dbf, OSchema oSchema, OClass vClass, OClass v, OClass e, GremlinSchema<?> schema) throws SchemaWriterException {
    //        GremlinProperty latitude = null;
    //        GremlinProperty longitude = null;
    //        for (GremlinProperty property : schema.getProperties()) {
    //
    //            Class<?> cls = property.getType();
    //
    //            try {
    //                OProperty prop = vClass.getProperty(property.getName());
    //
    //                // If prop is null, it does not exist, so let's create it
    //                if (prop == null) {
    //
    //                    // If this property is a LINK
    //                    if (property instanceof GremlinLinkProperty) {
    //
    //                        OClass eClass = getOrCreateClass(oSchema, e, property.getName());
    //
    //                        if (eClass.getProperty("out") == null) {
    //                            OProperty out = eClass.createProperty("out", OType.LINK);
    //                            out.setLinkedClass(vClass);
    //                        }
    //                        if (eClass.getProperty("in") == null) {
    //                            OProperty in = eClass.createProperty("in", OType.LINK);
    //                            OClass linkedClass = getOrCreateClass(oSchema, v, ((GremlinRelatedProperty) property).getRelatedSchema().getClassName());
    //                            in.setLinkedClass(linkedClass);
    //                        }
    //
    //                        break;
    //
    //                    } else if (property instanceof GremlinCollectionProperty) {
    //
    //                        OClass edgeClass = getOrCreateClass(oSchema, e, property.getName());
    //
    //                        if (edgeClass.getProperty("out") == null) {
    //                            OProperty out = edgeClass.createProperty("out", OType.LINK);
    //                            out.setLinkedClass(vClass);
    //                        }
    //                        if (edgeClass.getProperty("in") == null) {
    //                            OProperty in = edgeClass.createProperty("in", OType.LINK);
    //                            OClass linkedClass = getOrCreateClass(oSchema, v, ((GremlinRelatedProperty) property).getRelatedSchema().getClassName());
    //                            in.setLinkedClass(linkedClass);
    //                        }
    //
    //                        break;
    //
    //                    } else {
    //                        // Standard property, primitive, String, Enum, byte[]
    //                        OType oType = OType.getTypeByClass(cls);
    //                        if (oType != null) {
    //
    //                            prop = vClass.createProperty(property.getName(), oType);
    //                            switch (property.getIndex()) {
    //                            case UNIQUE:
    //                                prop.createIndex(OClass.INDEX_TYPE.UNIQUE);
    //                                break;
    //                            case NON_UNIQUE:
    //                                prop.createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
    //                                break;
    //                            case SPATIAL_LATITUDE:
    //                                latitude = property;
    //                                break;
    //
    //                            case SPATIAL_LONGITUDE:
    //                                longitude = property;
    //                                break;
    //                            }
    //                        }
    //                    }
    //                }
    //            } catch (OSchemaException e1) {
    //                LOGGER.warn(String.format("Could not create property %s of type %s", property, cls), e1);
    //            }
    //        }
    //
    //        if (latitude != null && longitude != null) {
    //            String indexName = schema.getClassName() + ".lat_lon";
    //            if (dbf.graphNoTx().getIndex(indexName, Vertex.class) == null) {
    //                try {
    //                    dbf.graphNoTx().command(new OCommandSQL(String.format("CREATE INDEX %s ON %s(%s,%s) SPATIAL ENGINE LUCENE", indexName, schema.getClassName(), latitude.getName(),
    //                                                                          longitude.getName()))).execute();
    //                } catch (Exception e1) {
    //                    e1.printStackTrace();
    //                }
    //
    //            }
    //        }
    //    }

}
