package org.springframework.data.gremlin.object.core.domain;

/**
 * Created by gman on 24/09/15.
 */
public class Pet {

    public enum TYPE {
        CAT,DOG,HORSE;
    }

    private String name;

    private TYPE type;

    public Pet() {
    }

    public Pet(String name, TYPE type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
}
