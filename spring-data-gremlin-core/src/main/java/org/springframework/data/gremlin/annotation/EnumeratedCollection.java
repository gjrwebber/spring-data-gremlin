package org.springframework.data.gremlin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashSet;

import static org.springframework.data.gremlin.annotation.Enumerated.EnumeratedType.ORDINAL;

/**
 * Created by gman on 12/08/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface EnumeratedCollection {

    Class<? extends Collection> value() default HashSet.class;

}
