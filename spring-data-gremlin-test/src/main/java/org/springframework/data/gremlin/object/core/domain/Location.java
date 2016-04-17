package org.springframework.data.gremlin.object.core.domain;

import com.tinkerpop.blueprints.Direction;
import org.springframework.data.gremlin.annotation.Id;
import org.springframework.data.gremlin.annotation.Index;
import org.springframework.data.gremlin.annotation.Link;
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

    @Link(value="location_in_area", direction = Direction.IN)
    private Area area;

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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
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
    //        Location location = (Location) o;
    //
    //        return !(id != null ? !id.equals(location.id) : location.id != null);
    //
    //    }
    //
    //    @Override
    //    public int hashCode() {
    //        return id != null ? id.hashCode() : 0;
    //    }

}
