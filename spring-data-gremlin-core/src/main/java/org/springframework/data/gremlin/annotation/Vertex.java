package org.springframework.data.gremlin.annotation;

import java.lang.annotation.*;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Vertex {

    /**
     * The type/name of the Vertex. If left blank the name of the Class is used.
     * @return
     */
    String value() default "";

    /**
     * pseudonym for value().
     * @return
     */
    String name() default "";

}
