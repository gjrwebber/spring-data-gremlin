package org.springframework.data.gremlin.schema;

import javax.persistence.*;

/**
 * Created by gman on 18/05/15.
 */
public class TestEntity {

    @Id
    private String id;

    @Column(name = "unique", unique = true)
    private String name;

    private int value;

    @Transient
    private String tranny;

    private transient int anotherTranny;

    @OneToOne
    private LinkedTestEntity linkedEntity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "embeddedValue"))})
    private EmbeddedTestEntity embeddedTestEntity;

}
