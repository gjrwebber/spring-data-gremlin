package org.springframework.data.gremlin.query.execution;

import com.tinkerpop.blueprints.Element;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.query.AbstractGremlinQuery;
import org.springframework.data.gremlin.query.CompositeResult;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
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
    public CollectionExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    /* (non-Javadoc)
         * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
         */
    @Override
    @SuppressWarnings("unchecked")
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        Class<?> mappedType = query.getQueryMethod().getReturnedObjectType();

        Iterable<Element> elements = (Iterable<Element>) query.runQuery(parameters, values);

        List<Object> objects = new ArrayList<Object>();
        if (mappedType.isAssignableFrom(Map.class)) {
            buildMapList(elements, objects);

        } else if (mappedType == CompositeResult.class) {
            Class<?> type = GenericsUtil.getGenericType(query.getQueryMethod().getMethod());
            GremlinSchema mapper = schemaFactory.getSchema(type);
            buildCompositeResults(mapper, elements, objects);
        } else {
            GremlinSchema mapper = schemaFactory.getSchema(mappedType);
            buildEntityList(mapper, elements, objects);
        }

        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        Pageable pageable = accessor.getPageable();
        if (pageable != null) {
            long total = (Long) new CountExecution(schemaFactory, parameters, graphAdapter).doExecute(query, values);
            return new PageImpl<Object>(objects, pageable, total);
        }

        return objects;
    }

    private void buildMapList(Iterable<Element> elements, List<Object> mapList) {

        for (Element element : elements) {
            Map<String, Object> map = elementToMap(element);
            mapList.add(map);
        }
    }

    private void buildCompositeResults(GremlinSchema mapper, Iterable<Element> elements, List<Object> resultList) {

        for (Element element : elements) {
            Map<String, Object> map = elementToMap(element);
            Object entity = mapper.loadFromGraph(graphAdapter, element);
            resultList.add(new CompositeResult<Object>(entity, map));
        }
    }

    private void buildEntityList(GremlinSchema mapper, Iterable<Element> elements, List<Object> objects) {
        for (Element element : elements) {
            objects.add(mapper.loadFromGraph(graphAdapter, element));
        }
    }
}
