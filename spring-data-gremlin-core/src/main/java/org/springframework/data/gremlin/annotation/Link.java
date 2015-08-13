package org.springframework.data.gremlin.annotation;

import com.tinkerpop.blueprints.Direction;

import java.lang.annotation.*;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Link {

    Direction value() default Direction.OUT;

    Direction direction() default Direction.OUT;
}
