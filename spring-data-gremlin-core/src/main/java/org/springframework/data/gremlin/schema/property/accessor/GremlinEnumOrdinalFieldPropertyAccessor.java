package org.springframework.data.gremlin.schema.property.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for enum properties that will be mapped with the ordinal (integer).
 *
 * @author Gman
 */
public class GremlinEnumOrdinalFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinEnumOrdinalFieldPropertyAccessor.class);

    private Class<? extends Enum> numnum;

    public GremlinEnumOrdinalFieldPropertyAccessor(Field field, Class<?> numnum) {
        super(field);
        this.numnum = (Class<? extends Enum>) numnum;
    }

    @Override
    public Integer get(Object object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting enum ordinal from " + object);
        }
        try {
            Object result = field.get(object);
            if (result == null) {
                return null;
            }
            Enum numnum = (Enum) result;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Got " + numnum + " with ordinal " + numnum.ordinal());
            }
            return numnum.ordinal();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, Integer ordinal) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting enum with ordinal " + ordinal + " on " + object);
        }
        try {
            if (ordinal == null) {
                field.set(object, null);
                return;
            }

            Object resultEnum = numnum.getEnumConstants()[ordinal];

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found " + resultEnum + " with ordinal " + ordinal + " for " + object);
            }
            field.set(object, resultEnum);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GremlinEnumOrdinalFieldPropertyAccessor{");
        sb.append("numnum=").append(numnum);
        sb.append('}');
        return sb.toString();
    }
}
