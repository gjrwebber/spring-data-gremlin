package org.springframework.data.gremlin.schema.writer.titan;

import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.writer.AbstractSchemaWriter;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.schema.writer.SchemaWriterException;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.titan.TitanGremlinGraphFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.springframework.data.gremlin.schema.property.GremlinRelatedProperty.CARDINALITY;

/**
 * A concrete {@link SchemaWriter} for an OrientDB database.
 *
 * @author Gman
 */
public class TitanSchemaWriter extends AbstractSchemaWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TitanSchemaWriter.class);

    private TitanManagement mgmt;

    public void initialise(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {

        try {
            TitanGraph graph = ((TitanGremlinGraphFactory) tgf).graph();
            mgmt = graph.openManagement();

        } catch (RuntimeException e) {
            String msg = String.format("Could not create schema %s. ERROR: %s", schema, e.getMessage());
            throw new SchemaWriterException(msg, e);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void writeSchema(GremlinGraphFactory tgf, GremlinSchema<?> schema) throws SchemaWriterException {
        initialise(tgf, schema);
        super.writeSchema(tgf, schema);
        mgmt.commit();
    }

    @Override
    protected boolean isPropertyAvailable(Object vertexClass, String name) {
         return ((TitanElement) vertexClass).keys().contains(name);
    }

    @Override
    protected Object createVertexClass(GremlinSchema schema) throws Exception {
        VertexLabel vertexClass = mgmt.getVertexLabel(schema.getClassName());
        if(vertexClass == null) {
            vertexClass = mgmt.makeVertexLabel(schema.getClassName()).make();
//            mgmt.commit();
        }
        return vertexClass;
    }

    @Override
    protected Object createEdgeClass(GremlinSchema schema) throws Exception {
        EdgeLabel edgeClass = mgmt.getEdgeLabel(schema.getClassName());
        if(edgeClass == null) {
            edgeClass = mgmt.makeEdgeLabel(schema.getClassName()).make();
//            mgmt.commit();
        }
        return edgeClass;
    }

    @Override
    protected void rollback(GremlinSchema schema) {

        try {
            mgmt.rollback();
        } catch (Exception e1) {
            LOGGER.error("Could not rollback: " + e1.getMessage(), e1);
        }

    }

    @Override
    protected Object createEdgeClass(String name, Object outVertex, Object inVertex, CARDINALITY cardinality) throws SchemaWriterException {

        Multiplicity multiplicity = Multiplicity.SIMPLE;
        if (cardinality == CARDINALITY.ONE_TO_ONE) {
            multiplicity = Multiplicity.ONE2ONE;
        } else if (cardinality == CARDINALITY.ONE_TO_MANY) {
            multiplicity = Multiplicity.ONE2MANY;
        }

//        EdgeLabel edgeLabel = mgmt.makeEdgeLabel(name).directed().multiplicity(multiplicity).make();
        EdgeLabel edgeLabel = mgmt.getOrCreateEdgeLabel(name);
        return edgeLabel;
    }

    @Override
    protected boolean isEdgeInProperty(Object edgeClass) {
        return true;
    }

    @Override
    protected boolean isEdgeOutProperty(Object edgeClass) {
        return true;
    }

    @Override
    protected Object setEdgeOut(Object edgeClass, Object vertexClass) {
        return null;
    }

    @Override
    protected Object setEdgeIn(Object edgeClass, Object vertexClass) {
        return null;
    }

    @Override
    protected Object createProperty(Object parentElement, String name, Class<?> cls) {
        if (cls == double.class) {
            cls = Double.class;
        }
        return mgmt.makePropertyKey(name).dataType(cls).make();
    }

    @Override
    protected void createNonUniqueIndex(Object prop) {
        PropertyKey property = (PropertyKey) prop;
        mgmt.buildIndex(property.name(), Vertex.class).addKey(property).buildCompositeIndex();
    }

    @Override
    protected void createUniqueIndex(Object prop) {
        PropertyKey property = (PropertyKey) prop;
        mgmt.buildIndex(property.name(), Vertex.class).addKey(property).unique().buildCompositeIndex();
    }

    @Override
    protected void createSpatialIndex(GremlinSchema<?> schema, GremlinProperty latitude, GremlinProperty longitude) {

    }

}
