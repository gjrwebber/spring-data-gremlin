package org.springframework.data.gremlin.schema.writer;

import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;

/**
 * Interface defining schema writer implementations.
 *
 * @author Gman
 */
public interface SchemaWriter {

    void writeSchema(GremlinGraphFactory dbf, GremlinSchema<?> schema) throws SchemaWriterException;

}
