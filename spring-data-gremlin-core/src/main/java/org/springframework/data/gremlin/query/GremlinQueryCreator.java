package org.springframework.data.gremlin.query;

import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Concrete {@link AbstractQueryCreator} for Gremlin.
 *
 * @author Gman
 */
public class GremlinQueryCreator extends AbstractQueryCreator<GraphTraversal, GraphTraversal> {

    private static final Logger logger = LoggerFactory.getLogger(GremlinQueryCreator.class);

    private final PartTree tree;

    private GremlinGraphFactory factory;

    private GremlinSchemaFactory schemaFactory;

    private ParameterAccessor accessor;

    private GremlinSchema schema;

    public GremlinQueryCreator(GremlinGraphFactory factory, GremlinSchemaFactory mapperfactory, Class<?> domainClass, PartTree tree, ParameterAccessor accessor) {
        super(tree, accessor);

        this.factory = factory;
        this.tree = tree;
        this.schemaFactory = mapperfactory;
        this.accessor = accessor;
        this.schema = schemaFactory.getSchema(domainClass);
    }

    @Override
    protected GraphTraversal create(Part part, Iterator<Object> iterator) {
        GraphTraversal base = new DefaultGraphTraversal();
        toCondition(base, part, iterator);
        return base;
    }

    @Override
    protected GraphTraversal and(Part part, GraphTraversal base, Iterator<Object> iterator) {
        //        Pipe lastPipe = (Pipe) base.getPipes().get(base.getPipes().size() - 1);
        //        if (lastPipe instanceof AndFilterPipe) {
        //            return base.add(toCondition(part, iterator));
        //        }
        //        GraphTraversalSource andPipeline = new GraphTraversalSource();
        toCondition(base, part, iterator);
        return base;
    }

    @Override
    protected GraphTraversal or(GraphTraversal base, GraphTraversal criteria) {
        return __.or(base, criteria);
    }

    public boolean isCountQuery() {
        return tree.isCountProjection();
    }

    @Override
    protected GraphTraversal complete(GraphTraversal criteria, Sort sort) {
        GraphTraversalSource source = factory.graph().traversal();
        return source.V().and(criteria);
    }

    protected void toCondition(final GraphTraversal pipeline, Part part, Iterator<Object> iterator) {

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
            pipeline.has(property, P.gt(iterator.next()));
            break;
        case GREATER_THAN_EQUAL:
            pipeline.has(property, P.gte(iterator.next()));
            break;
        case BEFORE:
        case LESS_THAN:
            pipeline.has(property, P.lt(iterator.next()));
            break;
        case LESS_THAN_EQUAL:
            pipeline.has(property, P.lte(iterator.next()));
            break;
        case BETWEEN:
            Object val = iterator.next();
            pipeline.and(__.has(property, P.lt(val)), __.has(property, P.gt(val)));
            break;
        case IS_NULL:
            pipeline.has(property);
            break;
        case IS_NOT_NULL:
            pipeline.has(property);
            break;
        case IN:
            pipeline.has(property, P.test(Contains.within, iterator.next()));
            break;
        case NOT_IN:
            pipeline.has(property, P.test(Contains.without, iterator.next()));
            break;
        case LIKE:
            pipeline.has(property, P.test(Like.IS, iterator.next()));
            break;
        case NOT_LIKE:
            pipeline.has(property, P.test(Like.NOT, iterator.next()));
            break;
        case STARTING_WITH:
            pipeline.has(property, P.test(StartsWith.DOES, iterator.next()));
            break;
        case ENDING_WITH:
            pipeline.has(property, P.test(EndsWith.DOES, iterator.next()));
            break;
        case CONTAINING:
            pipeline.has(property, P.test(Like.IS, iterator.next()));
            break;
        case SIMPLE_PROPERTY:
            pipeline.has(property, iterator.next());
            break;
        case NEGATING_SIMPLE_PROPERTY:
            pipeline.has(property, P.test(Compare.neq, iterator.next()));
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

    }


    private enum StartsWith implements BiPredicate<String, String> {
        DOES {
            @Override
            public boolean test(String o, String o2) {
                return o2.startsWith(o);
            }
        },
        NOT {
            @Override
            public boolean test(String o, String o2) {
                return !DOES.test(o, o2);
            }
        };
    }

    private enum EndsWith implements BiPredicate<String, String> {
        DOES {
            @Override
            public boolean test(String o, String o2) {
                return o2.endsWith(o);
            }
        },
        NOT {
            @Override
            public boolean test(String o, String o2) {
                return !DOES.test(o, o2);
            }
        };
    }

    private enum Like implements BiPredicate<String, String> {

        IS {
            @Override
            public boolean test(String s, String s2) {
                return s.toLowerCase().contains(s2.toLowerCase());
            }
        },
        NOT {
            @Override
            public boolean test(String s, String s2) {
                return !IS.test(s, s2);
            }
        };

    }
}
