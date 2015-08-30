package org.springframework.data.gremlin.schema.property;

import com.tinkerpop.blueprints.Direction;
import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.mapper.GremlinPropertyMapper;

import java.util.Collection;

/**
 * <p>
 * A {@link GremlinProperty} defining a property that is related to another mapped Class.
 * </p>
 * <p>
 * The related property must contain a related {@link GremlinSchema} which is provided by the {@link GremlinBeanPostProcessor} after all {@link GremlinSchema}s have been generated.
 * </p>
 * <p>
 * Cascading of related changes is also kept here.
 * </p>
 *
 * @author Gman
 */
public abstract class GremlinRelatedProperty<C> extends GremlinProperty<C> {

    public enum CASCADE_TYPE {
        READ,
        WRITE,
        ALL,
        NONE
    }

    public enum CARDINALITY {
        ONE_TO_ONE,
        ONE_TO_MANY,
        MANY_TO_ONE
    }

    private Direction direction;
    private GremlinSchema<C> relatedSchema;
    private GremlinRelatedProperty relatedProperty;
    private GremlinRelatedProperty adjacentProperty;
    private CARDINALITY cardinality;
    private CASCADE_TYPE cascadeType;

    public GremlinRelatedProperty(Class<C> cls, String name, Direction direction, GremlinPropertyMapper propertyMapper, CARDINALITY cardinality) {
        super(cls, name, propertyMapper);
        this.direction = direction;
        this.cardinality = cardinality;
    }

    public GremlinSchema<C> getRelatedSchema() {
        return relatedSchema;
    }

    public void setRelatedSchema(GremlinSchema<C> relatedSchema) {
        this.relatedSchema = relatedSchema;
        Collection<GremlinProperty> properties = relatedSchema.getPropertyForType(getType());
        if (properties != null) {
            for (GremlinProperty property : properties) {
                if (property instanceof GremlinRelatedProperty) {
                    GremlinRelatedProperty relProp = (GremlinRelatedProperty) property;
                    if (relProp.getDirection() == direction.opposite()) {
                        if (this.relatedProperty == null || relProp.getName().equals(getName())) {
                            this.relatedProperty = relProp;
                        }
                    }
                }
            }
        }

        if (relatedSchema.isEdgeSchema()) {

            for (Object propertyOfRelatedSchema : relatedSchema.getProperties()) {
                if (propertyOfRelatedSchema instanceof GremlinRelatedProperty) {
                    // If the property has the same direction of the given property here it
                    // means it is the opposite property of the @EntityRelationship
                    if (((GremlinRelatedProperty) propertyOfRelatedSchema).getDirection() == direction) {
                        adjacentProperty = (GremlinRelatedProperty) propertyOfRelatedSchema;
                        break;
                    }
                }
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public GremlinRelatedProperty getRelatedProperty() {
        return relatedProperty;
    }

    public GremlinRelatedProperty getAdjacentProperty() {
        return adjacentProperty;
    }

    public CASCADE_TYPE getCascadeType() {
        return cascadeType;
    }

    public void setCascadeType(CASCADE_TYPE cascadeType) {
        this.cascadeType = cascadeType;
    }

    public CARDINALITY getCardinality() {
        return cardinality;
    }
}
