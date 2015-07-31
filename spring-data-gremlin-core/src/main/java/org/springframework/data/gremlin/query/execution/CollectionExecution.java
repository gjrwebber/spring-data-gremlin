package org.springframework.data.gremlin.query.execution;

import com.tinkerpop.blueprints.Vertex;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.query.CompositeResult;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.utils.GenericsUtil;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes the query to return a collection of entities.
 *
 * @author Gman
 */
public class CollectionExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link org.springframework.data.gremlin.query.execution.CollectionExecution}.
     */
    public CollectionExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters) {
        super(schemaFactory, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        Class<?> mappedType = query.getQueryMethod().getReturnedObjectType();

        Iterable<Vertex> vertices = (Iterable<Vertex>) query.runQuery(parameters, values);

        List<Object> objects = new ArrayList<Object>();
        if (mappedType.isAssignableFrom(Map.class)) {
            buildMapList(vertices, objects);

        } else if (mappedType == CompositeResult.class) {
            Class<?> type = GenericsUtil.getGenericType(query.getQueryMethod().getMethod());
            GremlinSchema mapper = schemaFactory.getSchema(type);
            buildCompositeResults(mapper, vertices, objects);
        } else {
            GremlinSchema mapper = schemaFactory.getSchema(mappedType);
            buildEntityList(mapper, vertices, objects);
        }

        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        Pageable pageable = accessor.getPageable();
        if (pageable != null) {
            long total = (Long) new CountExecution(schemaFactory, parameters).doExecute(query, values);
            return new PageImpl<Object>(objects, pageable, total);
        }

        return objects;
    }

    private void buildMapList(Iterable<Vertex> vertices, List<Object> mapList) {

        for (Vertex vertex : vertices) {
            Map<String, Object> map = vertexToMap(vertex);
            mapList.add(map);
        }
    }

    private void buildCompositeResults(GremlinSchema mapper, Iterable<Vertex> vertices, List<Object> resultList) {

        for (Vertex vertex : vertices) {
            Map<String, Object> map = vertexToMap(vertex);
            Object entity = mapper.loadFromVertex(vertex);
            resultList.add(new CompositeResult<Object>(entity, map));
        }
    }

    private void buildEntityList(GremlinSchema mapper, Iterable<Vertex> vertices, List<Object> objects) {
        for (Vertex vertex : vertices) {
            objects.add(mapper.loadFromVertex(vertex));
        }
    }
}
