package org.springframework.data.gremlin.schema;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinIdFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;
import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;
import org.springframework.data.gremlin.schema.property.mapper.GremlinPropertyMapper;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (VERTEX, EDGE) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Gman
 */
public class GremlinEdgeSchema<V> extends GremlinSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinEdgeSchema.class);

    public GremlinEdgeSchema(Class<V> classType) {
        super(classType);
    }

    public GremlinEdgeSchema() {
        super();
    }

    private GremlinAdjacentProperty outProperty;
    private GremlinAdjacentProperty inProperty;

    public void addProperty(GremlinProperty property) {
        super.addProperty(property);
        if (property instanceof GremlinAdjacentProperty) {
            GremlinAdjacentProperty adjacentProperty = (GremlinAdjacentProperty) property;
            if (adjacentProperty.getDirection() == Direction.OUT) {
                outProperty = adjacentProperty;
            } else {
                inProperty = adjacentProperty;
            }
        }
    }

    public GremlinAdjacentProperty getOutProperty() {
        return outProperty;
    }

    public GremlinAdjacentProperty getInProperty() {
        return inProperty;
    }
}
