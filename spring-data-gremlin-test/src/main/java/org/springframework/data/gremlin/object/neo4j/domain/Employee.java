package org.springframework.data.gremlin.object.neo4j.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToOne;

//@Entity
public class Employee {

    @Id
    private String id;

    @Column(unique = true)
    private int employeeNumber;

    private String title;

    @OneToOne
    private Person person;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
