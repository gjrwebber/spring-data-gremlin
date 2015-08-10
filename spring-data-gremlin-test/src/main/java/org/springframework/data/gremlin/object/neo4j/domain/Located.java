package org.springframework.data.gremlin.object.neo4j.domain;

import org.springframework.data.neo4j.annotation.*;

import java.util.Date;

/**
 * Created by gman on 3/08/15.
 */
@RelationshipEntity(type = "was_located")
public class Located {

    @GraphId
    private String id;

    @GraphProperty(propertyName = "location_date")
    private Date date;

    @StartNode
    private Person person;

    @EndNode
    private Location location;

    public Located() {
    }

    public Located(Date date, Person person, Location location) {
        this.date = date;
        this.person = person;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
