package org.springframework.data.gremlin.object.jpa.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Person {

    public enum AWESOME {
        YES,
        NO;
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

    @ManyToOne
    @Column(name = "lives_at")
    private Address address;

    @OneToMany
    @Column(name = "located_at")
    private Set<Location> locations;

    private Boolean active;

    @Enumerated(EnumType.STRING)
    private AWESOME awesome = AWESOME.YES;

    private HashSet<VEHICLE> vehicles;

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

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public AWESOME getAwesome() {
        return awesome;
    }

    public void setAwesome(AWESOME awesome) {
        this.awesome = awesome;
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

}
