package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static com.tinkerpop.blueprints.Direction.*;
import static org.springframework.data.gremlin.annotation.Enumerated.EnumeratedType.STRING;

@Vertex
public class Person {

    public enum AWESOME {
        YES,
        NO
    }

    public enum VEHICLE {
        CAR,
        MOTORBIKE,
        BICYLE,
        SKATEBOARD,
        HOVERCRAFT,
        SPACESHIP
    }

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Link(name = "lives_at", direction = OUT)
    private Address address;

    @LinkVia
    private Set<Located> locations;

    @LinkVia
    private Located currentLocation;

    private Boolean active;

    @Enumerated(STRING)
    private AWESOME awesome = AWESOME.YES;

    private HashSet<VEHICLE> vehicles;

    @EnumeratedCollection(HashSet.class)
    private Set<VEHICLE> wantedVehicles;

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

    public Set<VEHICLE> getVehicles() {
        return vehicles;
    }

    public void addVehicle(VEHICLE vehicle) {
        if (vehicles == null) {
            vehicles = new HashSet<>();
        }
        vehicles.add(vehicle);
    }

    public Set<VEHICLE> getWantedVehicles() {
        return wantedVehicles;
    }

    public void addWantedVehicle(VEHICLE vehicle) {
        if (wantedVehicles == null) {
            wantedVehicles = new HashSet<>();
        }
        wantedVehicles.add(vehicle);
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
//        Person person = (Person) o;
//
//        return !(id != null ? !id.equals(person.id) : person.id != null);
//
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? id.hashCode() : 0;
//    }
}
