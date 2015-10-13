package org.springframework.data.gremlin.schema.property.accessor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by gman on 12/10/15.
 */
public class GremlinJSONFieldPropertyAccessorTest {

    @Test
    public void testGet() throws Exception {

        TestObject object = new TestObject();
        object.rectangles.add(new Rectangle(10, 10));

        GremlinJSONFieldPropertyAccessor accessor = new GremlinJSONFieldPropertyAccessor(TestObject.class.getDeclaredField("rectangles"), RectangleMixIn.class);

        String serialised = accessor.get(object);
        assertNotNull(serialised);
        assertEquals("[{\"width\":10,\"height\":10}]", serialised);
    }

    @Test
    public void testSet() throws Exception {

        String serialised = "[{\"width\":15,\"height\":15}]";

        TestObject object = new TestObject();

        GremlinJSONFieldPropertyAccessor accessor = new GremlinJSONFieldPropertyAccessor(TestObject.class.getDeclaredField("rectangles"), RectangleMixIn.class);

        accessor.set(object, serialised);
        assertNotNull(object.rectangles);
        assertEquals(1, object.rectangles.size());
        assertEquals(15, object.rectangles.get(0).getW());
        assertEquals(15, object.rectangles.get(0).getH());
    }

    public class TestObject {
        private List<Rectangle> rectangles = new ArrayList<>();
    }


}