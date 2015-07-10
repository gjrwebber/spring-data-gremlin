package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * A SpatialIndex annotation used by the schema generator to define a property as part of a spatial index.
 *
 * @author Gman
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpatialIndex {
    /**
     * Defines the latitudinal coordinate of the SpatialIndex
     */
    boolean latitude() default false;
    /**
     * Defines the longitudinal coordinate of the SpatialIndex
     */
    boolean longitude() default false;
}
