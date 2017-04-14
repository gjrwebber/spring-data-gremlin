package org.springframework.data.gremlin.query;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.utils.GraphUtil;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParametersParameterAccessor;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


/**
 * A concrete {@link AbstractGremlinQuery} which handles String based gremlin queries defined using the {@link org.springframework.data.gremlin.annotation.Query} annotation.
 *
 * @author Gman
 */
public class StringBasedGremlinQuery extends AbstractGremlinQuery {

    private static final Logger logger = LoggerFactory.getLogger(StringBasedGremlinQuery.class);

    private GremlinGraphFactory dbf;

    private String queryString;

    private boolean countQuery;

    private boolean modifyingQuery;

    public StringBasedGremlinQuery(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter, String query, GremlinQueryMethod method) {
        super(schemaFactory, method, graphAdapter);
        this.dbf = dbf;
        this.queryString = query;
        this.countQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().count();
        this.modifyingQuery = method.hasAnnotatedQuery() && method.getQueryAnnotation().modify();
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Object doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {

        ScriptEngine engine = new GremlinGroovyScriptEngine();
        Bindings bindings = engine.createBindings();
        Graph graph = dbf.graph();
        bindings.put("g", graph.traversal());
        bindings.put("graph", graph.traversal());
        bindings.put("G", graph.traversal());

        String queryString = this.queryString;

        for (Parameter param : parameters.getBindableParameters()) {
            String paramName = param.getName();
            String placeholder = param.getPlaceholder();
            Object val = values[param.getIndex()];
            if (paramName == null) {
                placeholder = "placeholder_" + param.getIndex();
                queryString = queryString.replaceFirst("\\?", placeholder);
                bindings.put(placeholder, val);
            } else {
                queryString = queryString.replaceFirst(placeholder, paramName);
                bindings.put(paramName, val);
            }
        }

        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        Pageable pageable = accessor.getPageable();
        if (pageable != null && !ignorePaging) {
            queryString = String.format("%s.range(%d, %d)", queryString, pageable.getOffset(), pageable.getOffset() + pageable.getPageSize() );
        }

        try {

            logger.info(GraphUtil.queryToString(graph, (GraphTraversal)engine.eval(queryString, bindings)));

            return engine.eval(queryString, bindings);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(String.format("Could not evaluate Gremlin query String %s. Error: %s ", queryString, e.getMessage()), e);
        }
    }

    @Override
    protected boolean isCountQuery() {
        return this.countQuery;
    }

    @Override
    protected boolean isModifyingQuery() {
        return this.modifyingQuery;
    }


}
