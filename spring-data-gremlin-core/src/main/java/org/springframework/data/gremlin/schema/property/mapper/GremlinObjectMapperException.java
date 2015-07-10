package org.springframework.data.gremlin.schema.property.mapper;

/**
 * @author Gman
 */
public class GremlinObjectMapperException extends Throwable {
    public GremlinObjectMapperException(String msg) {super(msg);}

    public GremlinObjectMapperException(String msg, Exception e) {
        super(msg, e);
    }
}
