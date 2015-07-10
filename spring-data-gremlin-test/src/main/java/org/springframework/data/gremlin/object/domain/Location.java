package org.springframework.data.gremlin.object.domain;

import org.springframework.data.gremlin.annotation.SpatialIndex;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by gman on 9/06/15.
 */
@Entity
public class Location {

    @Id
    private String id;

    @SpatialIndex(latitude = true)
    private double latitude;
    @SpatialIndex(longitude = true)
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
