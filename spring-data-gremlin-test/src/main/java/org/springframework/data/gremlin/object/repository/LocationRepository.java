package org.springframework.data.gremlin.object.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.repository.query.Param;
import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.domain.Location;
import org.springframework.data.gremlin.query.CompositeResult;
import org.springframework.data.gremlin.repository.GremlinRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by gman on 12/06/15.
 */
public interface LocationRepository extends GremlinRepository<Location> {

}
