package org.springframework.data.gremlin.schema.property.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.utils.GenericsUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for enum properties that will be mapped with the name (String).
 *
 * @author Gman
 */
public class GremlinEnumStringCollectionFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<String> {
    protected Class<? extends Enum> numnum;
    protected Class<Collection<Enum>> collectionCls;
    private boolean useOrdinal = false;

    public GremlinEnumStringCollectionFieldPropertyAccessor(Field field) {
        this(field, (Class<Collection<Enum>>)field.getType());
    }

    public GremlinEnumStringCollectionFieldPropertyAccessor(Field field, Class<Collection<Enum>> collectionCls) {
        this(field, collectionCls, false);
    }

    public GremlinEnumStringCollectionFieldPropertyAccessor(Field field, Class<Collection<Enum>> collectionCls, boolean useOrdinal) {
        super(field);
        this.collectionCls = collectionCls;
        numnum = (Class<? extends Enum>)GenericsUtil.getGenericType(field);
        this.useOrdinal = useOrdinal;
    }

    @Override
    public String get(Object object) {

        try {
            Object result = field.get(object);

            if (result == null) {
                return null;
            }
            Collection<? extends Enum> enums = (Collection<? extends Enum>)result;
            StringBuffer buffer = new StringBuffer();
            for (Enum num : enums) {
                if (useOrdinal) {
                    buffer.append(num.ordinal());
                } else {
                    buffer.append(num.toString());
                }
                buffer.append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            return buffer.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, String array) {
        try {
            if (array == null) {
                field.set(object, null);
                return;
            }

            Collection<Enum> collection = collectionCls.newInstance();

            for (String name : array.split(",")) {
                for (Enum num : numnum.getEnumConstants()) {
                    if (useOrdinal) {
                        int ordinal = Integer.valueOf(name);
                        if (ordinal == num.ordinal()) {
                            collection.add(num);
                        }
                    } else {
                        if (num.name().equals(name)) {
                            collection.add(num);
                        }
                    }
                }
            }

            field.set(object, collection);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
