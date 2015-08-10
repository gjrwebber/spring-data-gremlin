package org.springframework.data.gremlin.object.tests.orientdb.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractLocationRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDB_Neo4j_TestConfiguration.class)
public class OrientDB_Neo4j_SpatialRepositoryTest extends AbstractLocationRepositoryTest { }
