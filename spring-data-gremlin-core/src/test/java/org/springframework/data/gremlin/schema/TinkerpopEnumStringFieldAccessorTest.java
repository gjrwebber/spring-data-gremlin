package org.springframework.data.gremlin.schema;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.gremlin.schema.property.accessor.GremlinEnumStringFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;

import static org.junit.Assert.assertEquals;

/**
 * Created by gman on 18/05/15.
 */
public class TinkerpopEnumStringFieldAccessorTest {
    TestObject obj1;

    GremlinPropertyAccessor accessor;

    @Before
    public void setUp() throws Exception {
        obj1 = new TestObject();

        accessor = new GremlinEnumStringFieldPropertyAccessor(TestObject.class.getDeclaredField("test"), TestObject.TEST.class);
    }


    @Test
    public void should_read_all_variables() throws Exception {
        assertEquals("ONE", accessor.get(obj1));
    }

    @Test
    public void should_read_null_variables() throws Exception {
        obj1.test = null;
        assertEquals(null, accessor.get(obj1));
    }

    @Test
    public void should_write_all_variables() throws Exception {
        accessor.set(obj1, "TWO");
        assertEquals(obj1.test, TestObject.TEST.TWO);
    }

    @Test
    public void should_write_all_nulls() throws Exception {
        accessor.set(obj1, null);
        assertEquals(null, obj1.test);
    }

    static class TestObject {
        public enum TEST {
            ONE,
            TWO
        }
        TEST test = TEST.ONE;
    }

}