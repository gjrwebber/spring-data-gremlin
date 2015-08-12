package org.springframework.data.gremlin.schema;

/**
 * Created by gman on 4/08/15.
 */
public class GremlinRelationshipSchema<V> extends GremlinSchema<V> {

    private GremlinSchema<?> outSchema;
    private GremlinSchema<?> inSchema;

    public GremlinRelationshipSchema(Class<V> classType, GremlinSchema<?> outSchema, GremlinSchema<?> inSchema) {
        super(classType);
        this.outSchema = outSchema;
        this.inSchema = inSchema;
    }

    public GremlinRelationshipSchema(GremlinSchema<?> outSchema, GremlinSchema<?> inSchema) {
        this.outSchema = outSchema;
        this.inSchema = inSchema;
    }

    public GremlinSchema<?> getOutSchema() {
        return outSchema;
    }

    public void setOutSchema(GremlinSchema<?> outSchema) {
        this.outSchema = outSchema;
    }

    public GremlinSchema<?> getInSchema() {
        return inSchema;
    }

    public void setInSchema(GremlinSchema<?> inSchema) {
        this.inSchema = inSchema;
    }
}
