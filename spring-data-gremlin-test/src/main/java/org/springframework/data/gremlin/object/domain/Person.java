package org.springframework.data.gremlin.object.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Person {

    public enum AWESOME {
        YES, NO;
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
        address.getPeople().add(this);
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
}
