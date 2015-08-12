package org.springframework.data.gremlin.object.neo4j.domain;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import java.util.Set;

@NodeEntity
public class Person {

    public enum AWESOME {
        YES,
        NO
    }

    @GraphId
    private String id;

    private String firstName;

    private String lastName;

    @RelatedTo(type = "lives_at", direction = Direction.OUTGOING)
    private Address address;

    @RelatedToVia
    private Set<Located> locations;

    @RelatedToVia
    private Located currentLocation;

    private Boolean active;

    private AWESOME awesome = AWESOME.YES;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(String firstName, String lastName, Address address, Boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.active = active;
        if (address != null) {
            address.getPeople().add(this);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Located> getLocations() {
        return locations;
    }

    public void setLocations(Set<Located> locations) {
        this.locations = locations;
    }

    public AWESOME getAwesome() {
        return awesome;
    }

    public void setAwesome(AWESOME awesome) {
        this.awesome = awesome;
    }

    public Located getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Located currentLocation) {
        this.currentLocation = currentLocation;
    }
}
