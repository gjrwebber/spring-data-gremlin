package org.springframework.data.gremlin.object.tests.tinker.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractAddressRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Tinker_Neo4j_TestConfiguration.class)
public class Tinker_Neo4j_AddressRepositoryTest extends AbstractAddressRepositoryTest { }
