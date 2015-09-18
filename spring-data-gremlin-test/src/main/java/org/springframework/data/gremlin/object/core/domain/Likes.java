package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.Edge;
import org.springframework.data.gremlin.annotation.FromVertex;
import org.springframework.data.gremlin.annotation.Id;
import org.springframework.data.gremlin.annotation.ToVertex;

import java.util.Date;

/**
 * Created by gman on 14/09/15.
 */
@Edge
public class Likes {

    @Id
    private String id;

    private Date date = new Date();

    @FromVertex
    private Person person1;

    @ToVertex
    private Person person2;

    public Likes() {
    }

    public Likes(Person person1, Person person2) {
        this.person1 = person1;
        this.person2 = person2;
    }

    public Person getPerson1() {
        return person1;
    }

    public Person getPerson2() {
        return person2;
    }
}
