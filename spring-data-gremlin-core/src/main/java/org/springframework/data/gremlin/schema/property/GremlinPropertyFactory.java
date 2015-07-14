package org.springframework.data.gremlin.schema.property;

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

    public <V> GremlinProperty<V> getLinkedProperty(Class<V> cls, String name) {
        return new GremlinLinkProperty<V>(cls, name);
    }

    public <V> GremlinProperty<V> getCollectiondProperty(Class<V> cls, String name) {
        return new GremlinCollectionProperty<V>(cls, name);
    }

}
