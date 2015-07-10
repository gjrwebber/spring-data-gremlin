package org.springframework.data.gremlin.schema.generator.jpa;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.gremlin.schema.*;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinPropertyFactory;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by gman on 18/05/15.
 */
public class JpaSchemaGeneratorTest {

    JpaSchemaGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new JpaSchemaGenerator();
        generator.setEntities(TestEntity.class, LinkedTestEntity.class);
        generator.setEmbedded(EmbeddedTestEntity.class);
    }

    @Test
    public void should_generate_schema_for_TestEntity() throws Exception {
        GremlinSchema schema = generator.generateSchema(TestEntity.class);
        assertNotNull(schema);
        assertNotNull(schema.getIdAccessor());
        assertEquals(TestEntity.class, schema.getClassType());
        assertEquals("TestEntity", schema.getClassName());
        Collection<String> propNames = schema.getPropertyNames();
        assertEquals(5, propNames.size());

        assertTrue(propNames.contains("unique"));
        assertEquals(String.class, schema.getProperty("unique").getType());
        assertTrue(schema.getProperty("unique").getIndex() == GremlinProperty.INDEX.UNIQUE);

        assertTrue(propNames.contains("value"));
        assertEquals(int.class, schema.getProperty("value").getType());

        assertTrue(propNames.contains("linkedEntity"));
        assertTrue(schema.getProperty("linkedEntity") instanceof GremlinLinkProperty);
        assertEquals(LinkedTestEntity.class, schema.getProperty("linkedEntity").getType());

        assertTrue(propNames.contains("embeddedTestEntity_embeddedBla"));
        assertTrue(propNames.contains("embeddedTestEntity_embeddedDate"));

        assertFalse(propNames.contains("id"));
        assertFalse(propNames.contains("name"));
        assertFalse(propNames.contains("tranny"));
        assertFalse(propNames.contains("anotherTranny"));

    }

    @Test
    public void should_generate_schema_for_LinkedTestEntity() throws Exception {
        GremlinSchema schema = generator.generateSchema(LinkedTestEntity.class);
        assertNotNull(schema);
        assertNotNull(schema.getIdAccessor());
        assertEquals(LinkedTestEntity.class, schema.getClassType());
        assertEquals("Link", schema.getClassName());
        Collection<String> propNames = schema.getPropertyNames();
        assertEquals(0, propNames.size());
    }

}