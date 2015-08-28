package org.springframework.data.gremlin.schema;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;

import static org.junit.Assert.assertEquals;

/**
 * Created by gman on 18/05/15.
 */
public class GremlinFieldAccessorTest {
    TestObject obj1;

    GremlinPropertyAccessor primAccessor;
    GremlinPropertyAccessor strAccessor;
    GremlinPropertyAccessor objAccessor;

    @Before
    public void setUp() throws Exception {
        obj1 = new TestObject();

        strAccessor = new GremlinFieldPropertyAccessor(TestObject.class.getDeclaredField("str"));
        objAccessor = new GremlinFieldPropertyAccessor(TestObject.class.getDeclaredField("obj"));
        primAccessor = new GremlinFieldPropertyAccessor(TestObject.class.getDeclaredField("prim"));
    }


    @Test
    public void should_read_all_variables() throws Exception {
        assertEquals(10, primAccessor.get(obj1));
        assertEquals("bla", strAccessor.get(obj1));
        assertEquals(obj1.obj, objAccessor.get(obj1));
    }

    @Test
    public void should_read_null_variables() throws Exception {
        obj1.prim = 0;
        obj1.str = null;
        obj1.obj = null;
        assertEquals(0, primAccessor.get(obj1));
        assertEquals(null, strAccessor.get(obj1));
        assertEquals(null, objAccessor.get(obj1));
    }

    @Test
    public void should_write_all_variables() throws Exception {
        primAccessor.set(obj1, 55);
        strAccessor.set(obj1, "olb");
        TestObject2 obj3 = new TestObject2();
        objAccessor.set(obj1, obj3);


        assertEquals(obj1.prim, 55);
        assertEquals(obj1.str, "olb");
        assertEquals(obj1.obj, obj3);
    }

    @Test
    public void should_write_all_nulls() throws Exception {
        primAccessor.set(obj1, 0);
        strAccessor.set(obj1, null);
        objAccessor.set(obj1, null);


        assertEquals(obj1.prim, 0);
        assertEquals(obj1.str, null);
        assertEquals(obj1.obj, null);
    }

    class TestObject {
        int prim = 10;
        String str = "bla";
        TestObject2 obj = new TestObject2();
    }

    class TestObject2 { }
}
