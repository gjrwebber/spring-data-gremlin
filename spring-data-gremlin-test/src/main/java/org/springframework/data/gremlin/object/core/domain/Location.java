package org.springframework.data.gremlin.object.core.domain;

import org.springframework.data.gremlin.annotation.Id;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.annotation.Vertex;

import static org.springframework.data.gremlin.annotation.Index.IndexType.SPATIAL_LATITUDE;
import static org.springframework.data.gremlin.annotation.Index.IndexType.SPATIAL_LONGITUDE;

/**
 * Created by gman on 9/06/15.
 */
@Vertex
public class Location {

    @Id
    private String id;

    @Index(type = SPATIAL_LATITUDE)
    private double latitude;
    @Index(type = SPATIAL_LONGITUDE)
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
