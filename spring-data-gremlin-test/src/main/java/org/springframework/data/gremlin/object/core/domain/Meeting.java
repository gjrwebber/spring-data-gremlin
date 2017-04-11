package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.Link;
import org.springframework.data.gremlin.annotation.Vertex;

/**
 * @author Gman
 * @created 19/01/2016
 */
@Vertex
public class Meeting {

    @Link
    private Person person1;

    @Link
    private Person person2;

    public Meeting() {
    }

    public Meeting(Person person1, Person person2) {
        this.person1 = person1;
        this.person2 = person2;
    }

    public Person getPerson2() {
        return person2;
    }

    public void setPerson2(Person person2) {
        this.person2 = person2;
    }

    public Person getPerson1() {
        return person1;
    }

    public void setPerson1(Person person1) {
        this.person1 = person1;
    }
}
