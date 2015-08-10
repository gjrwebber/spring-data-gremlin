package org.springframework.data.gremlin.schema.property;

import com.tinkerpop.blueprints.Direction;
import org.springframework.data.gremlin.annotation.Index;

/**
 * Factory for {@link GremlinProperty}s.
 *
 * @author Gman
 */
public class GremlinPropertyFactory {

    public <V> GremlinProperty<V> getProperty(Class<V> cls, String name) {
        return getIndexedProperty(cls, name, Index.IndexType.NONE, null);
    }

    public <V> GremlinProperty<V> getIndexedProperty(Class<V> cls, String name, Index.IndexType index, String indexName) {
        return new GremlinProperty<V>(cls, name, index, indexName);
    }

    public <V> GremlinProperty<V> getUniqueProperty(Class<V> cls, String name) {
        return getIndexedProperty(cls, name, Index.IndexType.UNIQUE, null);
    }

    public <V> GremlinProperty<V> getLinkProperty(Class<V> cls, String name, Direction direction) {
        return new GremlinLinkProperty<V>(cls, name, direction);
    }

    public <V> GremlinProperty<V> getLinkViaProperty(Class<V> cls, String name, Direction direction) {
        return new GremlinLinkViaProperty<V>(cls, name, direction);
    }

    public <V> GremlinProperty<V> getAdjacentProperty(Class<V> cls, String name, Direction direction) {
        return new GremlinAdjacentProperty<V>(cls, name, direction);
    }

    public <V> GremlinProperty<V> getCollectionProperty(Class<V> cls, String name, Direction direction) {
        return new GremlinCollectionProperty<V>(cls, name, direction);
    }

    public <V> GremlinProperty<V> getCollectionViaProperty(Class<V> cls, String name, Direction direction) {
        return new GremlinCollectionViaProperty<V>(cls, name, direction);
    }

}
