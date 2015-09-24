package org.springframework.data.gremlin.object.core.domain;

import java.io.Serializable;

/**
 * Created by gman on 24/09/15.
 */
public class House implements Serializable {

    private int rooms;

    public House() {
    }

    public House(int rooms) {
        this.rooms = rooms;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }
}
