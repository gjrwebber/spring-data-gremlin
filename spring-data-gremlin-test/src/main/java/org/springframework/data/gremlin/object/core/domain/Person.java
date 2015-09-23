package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static com.tinkerpop.blueprints.Direction.OUT;
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

    @LinkVia
    private Set<Likes> likes;

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

    public Set<Likes> getLikes() {
        if (likes == null) {
            likes = new HashSet<Likes>();
        }
        return likes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("id='").append(id).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", active=").append(active);
        sb.append(", awesome=").append(awesome);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) {
            return false;
        }
        if (active != null ? !active.equals(person.active) : person.active != null) {
            return false;
        }
        return awesome == person.awesome;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (awesome != null ? awesome.hashCode() : 0);
        return result;
    }
}
