package org.springframework.data.gremlin.object.tests.janus.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractAddressRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */

@ContextConfiguration(classes = Janus_Neo4j_TestConfiguration.class)
public class Janus_Neo4j_AddressRepositoryTest extends AbstractAddressRepositoryTest { }
