package org.springframework.data.gremlin.object.jpa.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Address {

    @Id
    private String id;

    @Embedded
    private Country country;

    private String city;

    private String street;

    @OneToOne
    @Column(name = "of_area")
    private Area area;

    @OneToMany(mappedBy = "address")
    @Column(name = "lives_at")
    private Set<Person> people;

    public Address() {}

    public Address(Country country, String city, String street, Area area) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Set<Person> getPeople() {
        if (people == null) {
            people = new HashSet<Person>();
        }
        return people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
    }
}
