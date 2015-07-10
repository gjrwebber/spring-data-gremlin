package org.springframework.data.gremlin.query;

import com.tinkerpop.blueprints.Compare;
import com.tinkerpop.blueprints.Contains;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.filter.AndFilterPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Concrete {@link AbstractQueryCreator} for Gremlin.
 *
 * @author Gman
 */
public class GremlinQueryCreator extends AbstractQueryCreator<GremlinPipeline, GremlinPipeline> {

    private static final Logger logger = LoggerFactory.getLogger(GremlinQueryCreator.class);

    private final PartTree tree;

    private GremlinGraphFactory factory;

    private GremlinSchemaFactory schemaFactory;

    private ParameterAccessor accessor;

    public GremlinQueryCreator(GremlinGraphFactory factory, GremlinSchemaFactory mapperfactory, PartTree tree, ParameterAccessor accessor) {
        super(tree, accessor);

        this.factory = factory;
        this.tree = tree;
        this.schemaFactory = mapperfactory;
        this.accessor = accessor;
    }

    @Override
    protected GremlinPipeline create(Part part, Iterator<Object> iterator) {
        return toCondition(part, iterator);
    }

    @Override
    protected GremlinPipeline and(Part part, GremlinPipeline base, Iterator<Object> iterator) {
        Pipe lastPipe = (Pipe) base.getPipes().get(base.getPipes().size() - 1);
        if (lastPipe instanceof AndFilterPipe) {
            return base.add(toCondition(part, iterator));
        }
        GremlinPipeline andPipeline = new GremlinPipeline();
        andPipeline.and(base, toCondition(part, iterator));
        return andPipeline;
    }

    @Override
    protected GremlinPipeline or(GremlinPipeline base, GremlinPipeline criteria) {
        return new GremlinPipeline().or(base, criteria);
    }

    public boolean isCountQuery() {
        return tree.isCountProjection();
    }

    @Override
    protected GremlinPipeline complete(GremlinPipeline criteria, Sort sort) {
        Pageable pageable = accessor.getPageable();
        GremlinPipeline pipeline = new GremlinPipeline(factory.graph()).V().add(criteria);
        return pipeline;
    }

    protected GremlinPipeline toCondition(Part part, Iterator<Object> iterator) {

        final GremlinPipeline pipeline = new GremlinPipeline();
        String property = part.getProperty().getLeafProperty().getSegment();

        Spliterator<PropertyPath> it = part.getProperty().spliterator();
        it.forEachRemaining(new Consumer<PropertyPath>() {
            @Override
            public void accept(PropertyPath propertyPath) {

                if (propertyPath.hasNext()) {
                    String segment = propertyPath.getSegment();
                    Class<?> type = propertyPath.getOwningType().getType();
                    GremlinProperty gremlinProperty = schemaFactory.getSchema(type).getPropertyForFieldname(segment);
                    String projectedName = gremlinProperty.getName();
                    pipeline.outE(projectedName).inV();
                }
            }
        });

        switch (part.getType()) {
        case AFTER:
        case GREATER_THAN:
            pipeline.has(property, Compare.GREATER_THAN, iterator.next());
            break;
        case GREATER_THAN_EQUAL:
            pipeline.has(property, Compare.GREATER_THAN_EQUAL, iterator.next());
            break;
        case BEFORE:
        case LESS_THAN:
            pipeline.has(property, Compare.LESS_THAN, iterator.next());
            break;
        case LESS_THAN_EQUAL:
            pipeline.has(property, Compare.LESS_THAN_EQUAL, iterator.next());
            break;
        case BETWEEN:
            Object val = iterator.next();
            pipeline.has(property, Compare.LESS_THAN, val).has(property, Compare.GREATER_THAN, val);
            break;
        case IS_NULL:
            pipeline.has(property, null);
            break;
        case IS_NOT_NULL:
            pipeline.has(property);
            break;
        case IN:
            pipeline.has(property, Contains.IN, iterator.next());
            break;
        case NOT_IN:
            pipeline.has(property, Contains.NOT_IN, iterator.next());
            break;
        case LIKE:
            pipeline.has(property, Like.IS, iterator.next());
            break;
        case NOT_LIKE:
            pipeline.has(property, Like.NOT, iterator.next());
            break;
        case STARTING_WITH:
            pipeline.has(property, StartsWith.DOES, iterator.next());
            break;
        case ENDING_WITH:
            pipeline.has(property, EndsWith.DOES, iterator.next());
            break;
        case CONTAINING:
            pipeline.has(property, Like.IS, iterator.next());
            break;
        case SIMPLE_PROPERTY:
            pipeline.has(property, iterator.next());
            break;
        case NEGATING_SIMPLE_PROPERTY:
            pipeline.hasNot(property, iterator.next());
            break;
        case TRUE:
            pipeline.has(property, true);
            break;
        case FALSE:
            pipeline.has(property, false);
            break;
        default:
            throw new IllegalArgumentException("Unsupported keyword!");
        }

        return new GremlinPipeline().and(pipeline);
    }


    private enum StartsWith implements Predicate {
        DOES,
        NOT;

        public boolean evaluate(final Object first, final Object second) {
            if (first instanceof String && second instanceof String) {
                return this == DOES && ((String) second).startsWith((String) first);
            }
            return false;
        }
    }

    private enum EndsWith implements Predicate {
        DOES,
        NOT;

        public boolean evaluate(final Object first, final Object second) {

            if (first instanceof String && second instanceof String) {
                return this == DOES && ((String) second).endsWith((String) first);
            }
            return false;
        }
    }

    private enum Like implements Predicate {

        IS,
        NOT;

        public boolean evaluate(final Object first, final Object second) {
            if (first instanceof String && second instanceof String) {
                return this == IS && first.toString().toLowerCase().contains(second.toString().toLowerCase());
            }
            return false;
        }
    }
}
