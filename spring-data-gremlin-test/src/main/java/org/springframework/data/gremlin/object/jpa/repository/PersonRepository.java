package org.springframework.data.gremlin.object.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.jpa.domain.Person;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PersonRepository extends GremlinRepository<Person> {

    @Query(value = "graph.V().has('firstName', ?)")
    List<Person> findByFirstName(String firstName);

    @Query(value = "graph.V().has('firstName', :fn)")
    List<Person> findByFirstNameWithParam(@Param("fn") String firstName);

    @Query(value = "graph.V().has('firstName', ?)")
    Page<Person> findByFirstName(String firstName, Pageable pageable);

    @Query(value = "graph.V().has('firstName', ?)")
    Person findSingleByFirstName(String firstName);

    @Query(value = "graph.V().has('firstName', ?)")
    List<Map<String, Object>> findMapByFirstName(String firstName);

    @Query(value = "graph.V().has('firstName', ?)")
    Map<String, Object> findSingleMapByFirstName(String firstName);

    List<Person> findByLastName(String lastName);

    List<Person> findByLastNameLike(String lastName);

    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findByFirstNameOrLastName(String firstName, String lastName);

    List<Person> findByFirstNameLike(String string);

    List<Person> findByFirstNameStartsWith(String firstName);

    Long countByFirstName(String firstName);

    Long countByLastName(String lastName);

    //    @Detach(DetachMode.ENTITY)
    List<Person> findByAddress_City(String city);


    List<Person> findByLastNameOrAddress_City(String lastName, String city);

    List<Person> findByAddress_Area_Name(String name);

    List<Person> findByLastNameOrAddress_Area_Name(String lastName, String name);

    //    @FetchPlan("*:-1")
    //    List<Person> findByAddress_Country(String city);

    List<Person> findByActiveIsTrue();

    List<Person> findByActiveIsFalse();

    Page<Person> findByLastName(String lastName, Pageable pageable);


    @Query(value = "graph.V('lastName', ?)")
    Page<Person> queryLastName(String lastName, Pageable pageable);

}
