package org.springframework.data.gremlin.schema.writer;

/**
 * @author Gman
 */
public class SchemaWriterException extends Exception {
    public SchemaWriterException(String message) {
        super(message);
    }

    public SchemaWriterException(String message, Throwable cause) {
        super(message, cause);
    }
}
