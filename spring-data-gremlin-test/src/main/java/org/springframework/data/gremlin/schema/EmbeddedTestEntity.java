package org.springframework.data.gremlin.schema;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
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
