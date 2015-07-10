package org.springframework.data.gremlin.object.repository;

import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.domain.Person;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;

/**
 * Created by gman on 25/06/15.
 */
public interface NativePersonRepository extends GremlinRepositoryWithNativeSupport<Person> {

    @Query(value = "delete vertex from (select from Person where firstName <> ?)", nativeQuery = true, modify = true)
    Integer deleteAllExceptUser(String firstName);
}
