package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.mockito.Mockito;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;

import java.util.HashSet;

/**
 * Created by gman on 24/07/15.
 */
public class GremlinLinkToPropertyMapperTest {

    //    @Test
    public void testCopyToVertex() throws Exception {

        Object test = new Object();
        GremlinLinkProperty prop = new GremlinLinkProperty(Object.class, "test", Direction.OUT);
        GremlinGraphAdapter adapter = Mockito.mock(GremlinGraphAdapter.class);
        Vertex vertex = Mockito.mock(Vertex.class);

        Mockito.when(vertex.edges(Direction.OUT, "test")).thenReturn(new HashSet<Edge>().iterator());


        GremlinLinkPropertyMapper mapper = new GremlinLinkPropertyMapper(Direction.OUT);
        mapper.copyToVertex(prop, adapter, vertex, test, new HashSet<GremlinSchema>());

    }

    //    @Test
    public void testLoadFromVertex() throws Exception {

    }
}