package org.springframework.data.gremlin.object.core.domain;


import org.springframework.data.gremlin.annotation.Id;
import org.springframework.data.gremlin.annotation.Vertex;

@Vertex
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
