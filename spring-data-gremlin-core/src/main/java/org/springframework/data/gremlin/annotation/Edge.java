package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Edge {

    /**
     * The name of the Edge. If left blank the name of the Class is used.
     * @return
     */
    String value() default "";

    /**
     * pseudonym for value().
     * @return
     */
    String name() default "";

}
