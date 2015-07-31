package org.springframework.data.gremlin.schema.property;

import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.mapper.GremlinPropertyMapper;

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

    private GremlinSchema<C> relatedSchema;
    private CARDINALITY cardinality;
    private CASCADE_TYPE cascadeType;

    public GremlinRelatedProperty(Class<C> cls, String name, GremlinPropertyMapper propertyMapper, CARDINALITY cardinality) {
        super(cls, name, propertyMapper);
        this.cardinality = cardinality;
    }

    public GremlinSchema<C> getRelatedSchema() {
        return relatedSchema;
    }

    public void setRelatedSchema(GremlinSchema<C> relatedSchema) {
        this.relatedSchema = relatedSchema;
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
