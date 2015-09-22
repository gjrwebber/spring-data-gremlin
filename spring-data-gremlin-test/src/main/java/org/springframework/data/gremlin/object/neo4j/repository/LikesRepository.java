package org.springframework.data.gremlin.object.neo4j.repository;

import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.neo4j.domain.Likes;
import org.springframework.data.gremlin.repository.GremlinRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by gman on 4/06/15.
 */
public interface LikesRepository extends GremlinRepository<Likes> {


    List<Likes> findByPerson1_FirstName(String firstName);

    @Query(value = "graph.V().has('firstName', ?).outE('Likes').as('x').inV.filter{it.firstName == ?}.back('x')")
    List<Likes> findByLiking(String liker, String liked);

}
