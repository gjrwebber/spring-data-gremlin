package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * An Index annotation used by the schema generator to define a property with an index; unique or non-unique.
 *
 * @author Gman
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * Defines the name of the index
     */
    String[] value() default "";

    /**
     * Defines whether the index is for a unique property
     *
     * @return
     */
    boolean unique() default false;
}
