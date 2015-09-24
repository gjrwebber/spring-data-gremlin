package org.springframework.data.gremlin.schema;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.gremlin.schema.property.accessor.GremlinEnumStringCollectionFieldPropertyAccessor;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by gman on 18/05/15.
 */
public class GremlinEnumCollectionSetFieldAccessorTest {
    TestObject obj1;

    GremlinEnumStringCollectionFieldPropertyAccessor accessor;

    @Before
    public void setUp() throws Exception {
        obj1 = new TestObject();

        accessor = new GremlinEnumStringCollectionFieldPropertyAccessor(TestObject.class.getDeclaredField("test"));
    }

    @Test
    public void should_read_all_variables() throws Exception {
        obj1.test.add(TestObject.TEST.ONE);
        obj1.test.add(TestObject.TEST.TWO);

        String obj = accessor.get(obj1);
        assertTrue(obj.contains("ONE"));
        assertTrue(obj.contains("TWO"));
    }

    @Test
    public void should_read_null_variables() throws Exception {
        obj1.test = null;
        assertEquals(null, accessor.get(obj1));
    }

    @Test
    public void should_write_all_variables() throws Exception {
        accessor.set(obj1, "TWO,THREE");
        assertNotNull(obj1.test);
        boolean has2 = false;
        boolean has3 = false;
        for (TestObject.TEST testEnum : obj1.test) {
            if(testEnum == TestObject.TEST.TWO) {
               has2 = true;
            }
            if (testEnum == TestObject.TEST.THREE) {
                has3 = true;
            }
        }

        assertTrue(has2);
        assertTrue(has3);
    }

    @Test
    public void should_write_all_nulls() throws Exception {
        accessor.set(obj1, null);
        assertEquals(null, obj1.test);
    }

    static class TestObject {
        public enum TEST {
            ONE,
            TWO,
            THREE
        }

        HashSet<TEST> test = new HashSet<>();
    }

}