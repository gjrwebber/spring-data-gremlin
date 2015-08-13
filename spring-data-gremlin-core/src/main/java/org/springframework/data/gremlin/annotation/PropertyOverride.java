package org.springframework.data.gremlin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface PropertyOverride {

    /**
     * The name of the property to override
     * @return
     */
    String name() default "";

    /**
     * Override the property with this parameter
     * @return
     */
    Property property();
}
