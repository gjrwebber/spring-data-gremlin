package org.springframework.data.gremlin.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.data.gremlin.annotation.Enumerated.EnumeratedType.ORDINAL;
import static org.springframework.data.gremlin.annotation.Property.SerialisableType.STANDARD;

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

    /**
     * If the SerialisableType is JSON, then a mixin class can be provided.
     * @return
     */
    Class<?> jsonMixin() default Void.class;

    /**
     * (Optional) The type used when serialising the property.
     */
    SerialisableType type() default STANDARD;

    enum SerialisableType {
        STANDARD,
        SERIALIZABLE,
        JSON
    }
}
