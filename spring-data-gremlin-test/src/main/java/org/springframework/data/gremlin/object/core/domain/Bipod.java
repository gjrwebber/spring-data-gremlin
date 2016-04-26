package org.springframework.data.gremlin.object.core.domain;


import org.springframework.data.gremlin.annotation.Id;
import org.springframework.data.gremlin.annotation.Vertex;

@Vertex
public class Bipod<T extends Place> extends Animal<T> {

    @Id
    private String id;

    private String name;

    public Bipod() {
    }

    public Bipod(String name) {
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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        Area area = (Area) o;
//
//        return !(id != null ? !id.equals(area.id) : area.id != null);
//
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? id.hashCode() : 0;
//    }
}
