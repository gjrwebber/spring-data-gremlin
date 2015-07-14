package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * An Index annotation used by the schema generator to define a property with an index; unique or non-unique.
 *
 * @author Gman
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * Defines the name of the index
     */
    String[] value() default "";

    /**
     * Defines the type of index
     *
     * @return
     */
    IndexType type() default IndexType.NON_UNIQUE;

    enum IndexType {
        NONE,
        UNIQUE,
        NON_UNIQUE,
        SPATIAL_LATITUDE,
        SPATIAL_LONGITUDE
    }
}
