package org.springframework.data.gremlin.object.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Area {

    @Id
    private String id;

    private String name;

    public Area() {
    }

    public Area(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
