package org.springframework.data.gremlin.object.neo4j.domain;

import org.springframework.data.gremlin.annotation.Edge;
import org.springframework.data.gremlin.annotation.Id;

import java.util.Date;

/**
 * Created by gman on 16/09/15.
 */
@Edge("asdf")
public class Likes {

    @Id
    private String id;

    private Date date = new Date();

    public Likes(Person graham, Person lara) {}
}
