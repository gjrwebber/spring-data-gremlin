package org.springframework.data.gremlin.object.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.jpa.domain.Person;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;

/**
 * Created by gman on 25/06/15.
 */
public interface NativePersonRepository extends GremlinRepositoryWithNativeSupport<Person> {

    @Query(value = "delete vertex from (select from Person where firstName <> ?)", nativeQuery = true, modify = true)
    Integer deleteAllExceptUser(String firstName);


    @Query(value = "SELECT expand(in('located_at')) FROM (SELECT FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}])", nativeQuery = true)
    Page<Person> findNear(double latitude, double longitude, double radius, Pageable pageable);

}
