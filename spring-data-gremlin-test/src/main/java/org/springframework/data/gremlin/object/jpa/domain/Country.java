package org.springframework.data.gremlin.object.jpa.domain;


import javax.persistence.Embeddable;

/**
 * Created by gman on 12/08/15.
 */
@Embeddable
public class Country {

    private String name;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
