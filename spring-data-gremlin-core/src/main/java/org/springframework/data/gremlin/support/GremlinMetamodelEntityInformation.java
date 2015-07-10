package org.springframework.data.gremlin.support;

import org.springframework.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

/**
 * An {@link AbstractEntityInformation} for Gremlin.
 *
 * @param <T> The class type of the entity
 * @author Gman
 */
public class GremlinMetamodelEntityInformation<T> extends AbstractEntityInformation<T, String> {

    private GremlinFieldPropertyAccessor<String> idAccessor;

    public GremlinMetamodelEntityInformation(Class<T> domainClass, GremlinFieldPropertyAccessor<String> idAccessor) {
        super(domainClass);
        this.idAccessor = idAccessor;
    }

    public String getId(T entity) {
        return idAccessor.get(entity);
    }

    public Class<String> getIdType() {
        return String.class;
    }
}
