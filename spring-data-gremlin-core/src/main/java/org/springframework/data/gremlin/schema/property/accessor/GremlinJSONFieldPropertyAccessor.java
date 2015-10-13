package org.springframework.data.gremlin.schema.property.accessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.gremlin.utils.GenericsUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for Objects serialised as JSON.
 *
 * @author Gman
 */
public class GremlinJSONFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<String> {

    private ObjectMapper mapper = new ObjectMapper();
    private Class<?> serialisedType;

    public GremlinJSONFieldPropertyAccessor(Field field, Class<?> mixin) {
        super(field);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        serialisedType = field.getType();
        if (Collection.class.isAssignableFrom(serialisedType)) {
            serialisedType = GenericsUtil.getGenericType(field);
        }
        if(mixin != null) {
            mapper.setMixInAnnotations(MapUtils.putAll(new HashMap<Class<?>, Class<?>>(), new Class[]{ serialisedType, mixin }));
        }
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
                field.set(object, null);
                return;
            }

            Object obj;
            if (Collection.class.isAssignableFrom(field.getType())) {
                obj = mapper.readValue(serialized, mapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) field.getType(), serialisedType));
            } else {
                obj = mapper.readValue(serialized, field.getType());
            }
            field.set(object, obj);

        } catch (IllegalAccessException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
