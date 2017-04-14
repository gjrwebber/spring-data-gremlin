package org.springframework.data.gremlin.query.orientdb;

import com.orientechnologies.orient.core.command.OCommand;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.query.AbstractNativeGremlinQuery;
import org.springframework.data.gremlin.query.GremlinQueryMethod;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.orientdb.OrientDBGremlinGraphFactory;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete {@link AbstractNativeGremlinQuery} handling OrientDB native queries.
 *
 * @author Gman
 */
public class NativeOrientdbGremlinQuery extends AbstractNativeGremlinQuery {

    private OrientDBGremlinGraphFactory orientGraphFactory;

    public NativeOrientdbGremlinQuery(GremlinGraphFactory dbf, GremlinQueryMethod method, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter, String query) {
        super(dbf, method, schemaFactory, graphAdapter, query);
        this.orientGraphFactory = (OrientDBGremlinGraphFactory) dbf;
    }

    @Override
    protected Object doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {

        String queryString = query;
        Map<String, Object> params = new HashMap<String, Object>();
        if (parameters != null) {
            for (Object obj : parameters) {
                Parameter param = (Parameter) obj;
                Object val = values[param.getIndex()];
                if (val == null || val instanceof Pageable) {
                    continue;
                }
                String paramName = param.getName();
                String placeholder = param.getPlaceholder();
                if (paramName == null) {
                    paramName = "placeholder_" + param.getIndex();
                    queryString = queryString.replaceFirst("\\?", paramName);
                } else {
                    queryString = queryString.replaceFirst(placeholder, paramName);
                }

                params.put(paramName, val);
            }

            // TODO: Hack until OLuceneSpatialIndexManager.searchIntersect handles the context
            //        if (queryString.contains("$spatial")) {
            for (String key : params.keySet()) {
                queryString = queryString.replaceAll("\\b" + key + "\\b", convertObject(params.get(key)));
            }
            //        }

            ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
            Pageable pageable = accessor.getPageable();
            if (pageable != null && !ignorePaging) {
                queryString = String.format("%s SKIP %d LIMIT %d", queryString, pageable.getOffset(), pageable.getPageSize());
            }

        }

        return run(queryString, params);
    }

    public Object run(String queryString, Map<String, Object> params) {

        Assert.hasLength(queryString);

        try {

            // TODO I don't know how to pass parameters in the orientdb query
            Object result = orientGraphFactory.graph().executeSql(queryString);
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO: Hack until OLuceneSpatialIndexManager.searchIntersect handles the context
    private String convertObject(Object val) {
        if (val instanceof Date) {
            return "'" + formatter.format(val) + "'";
        } else if (val instanceof String) {
            return "'" + val + "'";
        } else if (val instanceof Double) {
            return val + "d";
        } else if (val instanceof Float) {
            return val + "f";
        } else {
            GremlinSchema schema = schemaFactory.getSchema(val.getClass());
            if(schema != null) {
                return schema.getGraphId(val);
            } else {
                return val.toString();
            }
        }
    }
}
