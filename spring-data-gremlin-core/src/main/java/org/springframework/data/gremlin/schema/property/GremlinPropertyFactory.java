package org.springframework.data.gremlin.schema.property;

/**
 * Factory for {@link GremlinProperty}s.
 *
 * @author Gman
 */
public class GremlinPropertyFactory {

    public <V> GremlinProperty<V> getProperty(Class<V> cls, String name) {
        return getIndexedProperty(cls, name, GremlinProperty.INDEX.NONE, null);
    }

    public <V> GremlinProperty<V> getIndexedProperty(Class<V> cls, String name, GremlinProperty.INDEX index, String indexName) {
        return new GremlinProperty<V>(cls, name, index, indexName);
    }

    public <V> GremlinProperty<V> getUniqueProperty(Class<V> cls, String name) {
        return getIndexedProperty(cls, name, GremlinProperty.INDEX.UNIQUE, null);
    }

    public <V> GremlinProperty<V> getLinkedProperty(Class<V> cls, String name) {
        return new GremlinLinkProperty<V>(cls, name);
    }

    public <V> GremlinProperty<V> getCollectiondProperty(Class<V> cls, String name) {
        return new GremlinCollectionProperty<V>(cls, name);
    }

}
