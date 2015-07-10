package org.springframework.data.gremlin.schema.generator;

/**
 * @author Gman
 */
public class SchemaGeneratorException extends Exception {
    public SchemaGeneratorException(String message) {
        super(message);
    }

    public SchemaGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
