package org.springframework.data.gremlin.schema.writer.orientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.OMetadataDefault;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.impls.orient.OrientEdgeType;
import org.apache.tinkerpop.gremlin.structure.impls.orient.OrientGraphNoTx;
import org.apache.tinkerpop.gremlin.structure.impls.orient.OrientVertexType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.TestEntity;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.tx.orientdb.OrientDBGremlinGraphFactory;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gman on 18/05/15.
 */
public class OrientDbSchemaWriterTest {

    ODatabaseDocumentTx db;
    OrientDBGremlinGraphFactory dbf;
    OSchemaProxy oSchema;
    OrientGraphNoTx noTx;
    SchemaWriter writer;
    OrientVertexType v;
    OrientEdgeType e;
    OClass clazz;

    @Before
    public void setUp() throws Exception {

        clazz = Mockito.mock(OClass.class);
        noTx = Mockito.mock(OrientGraphNoTx.class);
        db = Mockito.mock(ODatabaseDocumentTx.class);
        v = Mockito.mock(OrientVertexType.class);
        e = Mockito.mock(OrientEdgeType.class);

        dbf = Mockito.mock(OrientDBGremlinGraphFactory.class);
        when(dbf.graphNoTx()).thenReturn(noTx);
        when(noTx.getRawGraph()).thenReturn(db);
        when(noTx.getVertexBaseType()).thenReturn(v);
        when(noTx.getEdgeBaseType()).thenReturn(e);
        OMetadataDefault metadataDefault = Mockito.mock(OMetadataDefault.class);
        when(db.getMetadata()).thenReturn(metadataDefault);
        oSchema = Mockito.mock(OSchemaProxy.class);
        when(metadataDefault.getSchema()).thenReturn(oSchema);
        writer = new OrientDbSchemaWriter();

        when(oSchema.getOrCreateClass("ClassName", v)).thenReturn(clazz);

        when(clazz.getSuperClass()).thenReturn(v);
        when(clazz.getName()).thenReturn("ClassName");
        when(v.getName()).thenReturn("V");
        when(e.getName()).thenReturn("E");
    }

    @Test
    public void should_write_string() throws Exception {

        GremlinSchema schema = Mockito.mock(GremlinSchema.class);
        when(schema.getClassName()).thenReturn("ClassName");
        when(schema.isVertexSchema()).thenReturn(true);

        GremlinProperty property1 = new GremlinProperty(String.class, "bla");

        when(schema.getProperties()).thenReturn(Arrays.asList(property1));

        writer.writeSchema(dbf, schema);
        verify(oSchema).getOrCreateClass("ClassName", v);
        verify(clazz).getProperty("bla");
        verify(clazz).createProperty("bla", OType.STRING);

    }

    @Test
    public void should_write_unique_string() throws Exception {

        GremlinSchema schema = Mockito.mock(GremlinSchema.class);
        when(schema.getClassName()).thenReturn("ClassName");
        when(schema.isVertexSchema()).thenReturn(true);

        GremlinProperty property1 = new GremlinProperty(String.class, "bla");
        property1.setIndex(Index.IndexType.UNIQUE);
        when(schema.getProperties()).thenReturn(Arrays.asList(property1));


        OrientVertexType.OrientVertexProperty blaProp = Mockito.mock(OrientVertexType.OrientVertexProperty.class);
        when(clazz.createProperty("bla", OType.STRING)).thenReturn(blaProp);

        writer.writeSchema(dbf, schema);
        verify(oSchema).getOrCreateClass("ClassName", v);
        verify(clazz).getProperty("bla");
        verify(clazz).createProperty("bla", OType.STRING);
        verify(blaProp).createIndex(OClass.INDEX_TYPE.UNIQUE);

    }

    @Test
    public void should_write_link() throws Exception {

        GremlinSchema schema = Mockito.mock(GremlinSchema.class);
        when(schema.getClassName()).thenReturn("ClassName");
        when(schema.isVertexSchema()).thenReturn(true);

        GremlinLinkProperty property3 = new GremlinLinkProperty(TestEntity.class, "link", Direction.OUT);
        GremlinSchema relatedSchema = Mockito.mock(GremlinSchema.class);
        when(relatedSchema.getClassName()).thenReturn("TestEntity");
        when(relatedSchema.isVertexSchema()).thenReturn(true);

        OClass linkClass = Mockito.mock(OClass.class);
        when(linkClass.getSuperClass()).thenReturn(v);
        when(linkClass.getName()).thenReturn("TestEntity");
        when(oSchema.getOrCreateClass("TestEntity", v)).thenReturn(linkClass);

        property3.setRelatedSchema(relatedSchema);
        when(schema.getProperties()).thenReturn(Arrays.asList(property3));

        OClass outClazz = Mockito.mock(OClass.class);
        when(oSchema.getOrCreateClass("link", e)).thenReturn(outClazz);
        when(outClazz.getSuperClass()).thenReturn(e);
        when(outClazz.getName()).thenReturn("link");

        OProperty outProperty = Mockito.mock(OProperty.class);
        when(outClazz.createProperty("out", OType.LINK)).thenReturn(outProperty);

        OProperty inProperty = Mockito.mock(OProperty.class);
        when(outClazz.createProperty("in", OType.LINK)).thenReturn(inProperty);

        writer.writeSchema(dbf, schema);
        verify(oSchema).getOrCreateClass("ClassName", v);
        verify(oSchema).getOrCreateClass("link", e);
        //        verify(oSchema).getOrCreateClass("other", e);
        verify(outClazz).createProperty("out", OType.LINK);
        verify(outClazz).createProperty("in", OType.LINK);
        verify(outProperty).setLinkedClass(clazz);
        verify(inProperty).setLinkedClass(linkClass);

    }

    @Test
    public void should_write_multiple_properties() throws Exception {

        GremlinSchema schema = Mockito.mock(GremlinSchema.class);
        when(schema.getClassName()).thenReturn("ClassName");
        when(schema.isVertexSchema()).thenReturn(true);

        GremlinProperty property1 = new GremlinProperty(String.class, "bla");
        property1.setIndex(Index.IndexType.UNIQUE);

        GremlinLinkProperty property2 = new GremlinLinkProperty(TestEntity.class, "link", Direction.OUT);
        GremlinSchema relatedSchema = Mockito.mock(GremlinSchema.class);
        when(relatedSchema.getClassName()).thenReturn("TestEntity");
        when(relatedSchema.isVertexSchema()).thenReturn(true);

        OClass linkClass = Mockito.mock(OClass.class);
        when(linkClass.getSuperClass()).thenReturn(v);
        when(linkClass.getName()).thenReturn("TestEntity");
        when(oSchema.getOrCreateClass("TestEntity", v)).thenReturn(linkClass);
        property2.setRelatedSchema(relatedSchema);

        OrientVertexType.OrientVertexProperty blaProp = Mockito.mock(OrientVertexType.OrientVertexProperty.class);
        when(clazz.createProperty("bla", OType.STRING)).thenReturn(blaProp);

        OClass linkEdge = Mockito.mock(OClass.class);
        when(linkEdge.getSuperClass()).thenReturn(e);
        when(linkEdge.getName()).thenReturn("link");
        when(oSchema.getOrCreateClass("link", e)).thenReturn(linkEdge);

        OProperty outProperty = Mockito.mock(OProperty.class);
        when(linkEdge.createProperty("out", OType.LINK)).thenReturn(outProperty);

        OProperty inProperty = Mockito.mock(OProperty.class);
        when(linkEdge.createProperty("in", OType.LINK)).thenReturn(inProperty);

        when(schema.getProperties()).thenReturn(Arrays.asList(property1, property2));

        writer.writeSchema(dbf, schema);
        verify(oSchema).getOrCreateClass("ClassName", v);
        verify(clazz).getProperty("bla");
        verify(clazz).createProperty("bla", OType.STRING);
        verify(blaProp).createIndex(OClass.INDEX_TYPE.UNIQUE);
        verify(oSchema).getOrCreateClass("link", e);
        verify(linkEdge).createProperty("out", OType.LINK);
        verify(linkEdge).createProperty("in", OType.LINK);
        verify(clazz).getProperty("link");

    }

}