package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * The annotation to declare custom queries directly on repository methods.
 *
 * @author Gman
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * Defines the Gremlin query to be executed when the annotated method` is called.
     */
    String value() default "";

    /**
     * Defines a count query that returns the number of elements in a query result.
     */
    boolean count() default false;

    /**
     * Defines a modification query which results in a modification of the database, returning the number of elements modified.
     */
    boolean modify() default false;

    /**
     * Instructs the interpreter that this is a native query for the underlying graph database.
     */
    boolean nativeQuery() default false;
}
