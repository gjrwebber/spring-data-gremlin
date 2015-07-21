package org.springframework.data.gremlin.schema;

import com.sun.org.apache.xpath.internal.operations.Mult;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by gman on 18/05/15.
 */
@Embeddable
public class EmbeddedTestEntity {

    private String embeddedBla;
    private Date embeddedDate;
    private int value;

    @Embedded
    private MultiEmbeddedTestEntity multiEmbed;
}
