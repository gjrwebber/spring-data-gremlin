package org.springframework.data.gremlin.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.PartTree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A concrete {@link AbstractGremlinQuery} implementation based on a {@link PartTree}.
 *
 * @author Gman
 */
public class PartTreeGremlinQuery extends AbstractGremlinQuery {

    /** The domain class. */
    private final Class<?> domainClass;

    /** The tree. */
    private final PartTree tree;

    private final GremlinQueryMethod method;

    private final GremlinGraphFactory dbf;

    private final GremlinGraphAdapter graphAdapter;

    /**
     * Instantiates a new {@link PartTreeGremlinQuery} from given {@link GremlinQueryMethod}.
     *
     * @param graphAdapter
     * @param method the query method
     */
    public PartTreeGremlinQuery(GremlinGraphFactory dbf, GremlinSchemaFactory schemaFactory, GremlinGraphAdapter graphAdapter, GremlinQueryMethod method) {
        super(schemaFactory, method, graphAdapter);

        this.dbf = dbf;
        this.graphAdapter = graphAdapter;
        this.method = method;
        this.domainClass = method.getEntityInformation().getJavaType();
        this.tree = new PartTree(method.getName(), domainClass);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.AbstractOrientQuery#doRunQuery(java.lang.Object[])
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected Object doRunQuery(DefaultParameters parameters, Object[] values, boolean ignorePaging) {
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);

        GremlinQueryCreator creator = new GremlinQueryCreator(dbf, schemaFactory, domainClass, tree, accessor);

        GraphTraversal pipeline = creator.createQuery();
        Pageable pageable = accessor.getPageable();
        if (pageable != null && !ignorePaging) {
            return pipeline.range(pageable.getOffset(), pageable.getOffset() + pageable.getPageSize());
        }
        return pipeline;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.AbstractOrientQuery#isCountQuery()
     */
    @Override
    protected boolean isCountQuery() {
        return tree.isCountProjection();
    }

    @Override
    protected boolean isModifyingQuery() {
        return tree.isDelete();
    }


}
