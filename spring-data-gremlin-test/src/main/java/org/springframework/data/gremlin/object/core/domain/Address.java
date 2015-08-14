package org.springframework.data.gremlin.object.core.domain;

import com.tinkerpop.blueprints.Direction;
import org.springframework.data.gremlin.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Vertex
public class Address {

    @Id
    private String id;

    @Embed(propertyOverrides = { @PropertyOverride(name = "name", property = @Property("countryName")) })
    private Country country;

    private String city;

    private String street;

    @Link(name = "of_area")
    private Area area;

    @Link(name = "lives_at", direction = Direction.IN)
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
