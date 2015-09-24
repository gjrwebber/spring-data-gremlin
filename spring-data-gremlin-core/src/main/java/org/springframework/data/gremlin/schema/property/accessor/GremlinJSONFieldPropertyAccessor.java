package org.springframework.data.gremlin.schema.property.accessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.gremlin.utils.GenericsUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for Objects serialised as JSON.
 *
 * @author Gman
 */
public class GremlinJsonFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<String> {

    private ObjectMapper mapper = new ObjectMapper();

    public GremlinJsonFieldPropertyAccessor(Field field) {
        super(field);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public String get(Object object) {

        try {
            Object result = field.get(object);

            if (result == null) {
                return null;
            }

            return mapper.writeValueAsString(result);
        } catch (IllegalAccessException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, String serialized) {
        try {
            if (serialized == null) {
                field.set(object, serialized);
                return;
            }

            Object obj;
            if (Collection.class.isAssignableFrom(field.getType())) {
                obj = mapper.readValue(serialized, mapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) field.getType(), GenericsUtil.getGenericType(field)));
            } else {
                obj = mapper.readValue(serialized, field.getType());
            }
            field.set(object, obj);

        } catch (IllegalAccessException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
