package org.springframework.data.gremlin.schema.property.accessor;

import org.springframework.data.gremlin.annotation.Property;

import java.io.*;
import java.lang.reflect.Field;

/**
 * A {@link AbstractGremlinFieldPropertyAccessor} for Serializable properties.
 *
 * @author Gman
 */
public class GremlinSerializableFieldPropertyAccessor extends AbstractGremlinFieldPropertyAccessor<byte[]> {

    public GremlinSerializableFieldPropertyAccessor(Field field) {
        super(field);
    }

    @Override
    public byte[] get(Object object) {

        try {
            Object result = field.get(object);

            if (result == null) {
                return null;
            }

            return toArray(result);
        } catch (IllegalAccessException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object object, byte[] serialized) {
        try {
            if (serialized == null) {
                field.set(object, serialized);
                return;
            }

            field.set(object, fromArray(serialized));

        } catch (IllegalAccessException | IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Object fromArray(byte[] bytes) throws IOException, ClassNotFoundException {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object obj = ois.readObject();
            ois.close();
            return obj;
    }

    private byte[] toArray(Object serializable) throws IOException {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(serializable);
            oos.close();

            return out.toByteArray();
    }
}
