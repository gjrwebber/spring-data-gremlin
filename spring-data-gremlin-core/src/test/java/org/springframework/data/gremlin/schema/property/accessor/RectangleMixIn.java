package org.springframework.data.gremlin.schema.property.accessor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gman on 12/10/15.
 */
abstract class RectangleMixIn {
    @JsonCreator
    RectangleMixIn(@JsonProperty("width") int w, @JsonProperty("height") int h) { }

    // note: could alternatively annotate fields "w" and "h" as well -- if so, would need to @JsonIgnore getters
    @JsonProperty("width")
    abstract int getW(); // rename property

    @JsonProperty("height")
    abstract int getH(); // rename property

    @JsonIgnore
    abstract int getSize(); // we don't need it!

}
