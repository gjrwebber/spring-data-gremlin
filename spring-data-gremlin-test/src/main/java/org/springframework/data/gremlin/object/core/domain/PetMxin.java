package org.springframework.data.gremlin.object.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gman on 12/10/15.
 */
abstract class PetMxin {

    PetMxin(@JsonProperty("name") String name, @JsonProperty("type") Pet.TYPE type) { }

    @JsonProperty("name")
    abstract String getName(); // rename property

    @JsonProperty("type")
    abstract Pet.TYPE getType(); // rename property

}
