package org.springframework.data.gremlin.schema.property.encoder.orientdb;

import org.springframework.data.gremlin.schema.property.encoder.GremlinPropertyEncoder;

/**
 * Created by gman on 23/06/15.
 */
public class OrientDbIdEncoder implements GremlinPropertyEncoder {

    @Override
    public Object encode(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString().replace('#', 'c').replace(':', 'p');
    }

    @Override
    public Object decode(Object id) {
        if (id == null) {
            return null;
        }
        return id.toString().replace('c', '#').replace('p', ':');
    }
}
