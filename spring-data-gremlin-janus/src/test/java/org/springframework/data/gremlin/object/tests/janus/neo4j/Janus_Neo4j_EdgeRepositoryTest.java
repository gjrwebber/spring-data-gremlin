package org.springframework.data.gremlin.object.tests.janus.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractEdgeRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by mmichail (zifnab87) on 4/14/2017.
 */
@ContextConfiguration(classes = Janus_Neo4j_TestConfiguration.class)
public class Janus_Neo4j_EdgeRepositoryTest extends AbstractEdgeRepositoryTest { }
