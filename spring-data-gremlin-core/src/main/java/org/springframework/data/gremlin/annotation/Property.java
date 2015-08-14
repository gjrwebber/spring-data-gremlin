package org.springframework.data.gremlin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Property {

    /**
     * The name of the Property. If left blank the name of the field is used.
     * @return
     */
    String value() default "";

    /**
     * pseudonym for value().
     * @return
     */
    String name() default "";
}
