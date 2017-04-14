package org.springframework.data.gremlin.annotation;


import org.apache.tinkerpop.gremlin.structure.Direction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface LinkVia {

    String value() default "";

    String name() default "";

    Direction direction() default Direction.OUT;
}
