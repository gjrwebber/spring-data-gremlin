package org.springframework.data.gremlin.schema;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by gman on 18/05/15.
 */
@Entity(name = "Link")
public class LinkedTestEntity {

    @Id
    private String id;

}
