package org.springframework.data.gremlin.schema.generator.jpa;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.schema.*;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by gman on 18/05/15.
 */
public class JpaSchemaGeneratorTest {

    JpaSchemaGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new JpaSchemaGenerator();
        generator.setEntities(TestEntity.class, LinkedTestEntity.class);
        generator.setEmbedded(EmbeddedTestEntity.class, MultiEmbeddedTestEntity.class);
    }

    @Test
    public void should_generate_schema_for_TestEntity() throws Exception {
        GremlinSchema schema = generator.generateSchema(TestEntity.class);
        assertNotNull(schema);
        assertNotNull(schema.getIdAccessor());
        assertEquals(TestEntity.class, schema.getClassType());
        assertEquals("TestEntity", schema.getClassName());
        Collection<String> propNames = schema.getPropertyNames();
        assertEquals(7, propNames.size());

        assertTrue(propNames.contains("unique"));
        assertEquals(String.class, schema.getProperty("unique").getType());
        assertTrue(schema.getProperty("unique").getIndex() == Index.IndexType.UNIQUE);

        assertTrue(propNames.contains("value"));
        assertEquals(int.class, schema.getProperty("value").getType());

        assertTrue(propNames.contains("linkedEntity"));
        assertTrue(schema.getProperty("linkedEntity") instanceof GremlinLinkProperty);
        assertEquals(LinkedTestEntity.class, schema.getProperty("linkedEntity").getType());

        assertTrue(propNames.contains("embeddedBla"));
        assertTrue(propNames.contains("embeddedDate"));
        assertTrue(propNames.contains("multiEmbedded"));
        assertTrue(propNames.contains("embeddedValue"));

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
