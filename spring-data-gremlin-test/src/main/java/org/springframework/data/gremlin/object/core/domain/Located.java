package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.*;

import java.util.Date;

/**
 * Created by gman on 3/08/15.
 */
@Edge("was_located")
public class Located {

    @Id
    private String id;

    @Property("location_date")
    private Date date;

    @FromVertex
    private Person person;

    @ToVertex
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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        Located located = (Located) o;
//
//        return !(id != null ? !id.equals(located.id) : located.id != null);
//
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? id.hashCode() : 0;
//    }
}
