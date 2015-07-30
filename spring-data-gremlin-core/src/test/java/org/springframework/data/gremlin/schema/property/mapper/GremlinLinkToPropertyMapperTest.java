package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
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

        Mockito.when(vertex.getEdges(Direction.OUT, "test")).thenReturn(new HashSet<Edge>());


        GremlinLinkPropertyMapper mapper = new GremlinLinkPropertyMapper(Direction.OUT);
        mapper.copyToVertex(prop, adapter, vertex, test, new HashSet<GremlinSchema>());

    }

    //    @Test
    public void testLoadFromVertex() throws Exception {

    }
}