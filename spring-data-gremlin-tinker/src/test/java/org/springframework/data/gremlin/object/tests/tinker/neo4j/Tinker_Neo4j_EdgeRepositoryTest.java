package org.springframework.data.gremlin.object.tests.tinker.neo4j;

import org.springframework.data.gremlin.object.core.repository.AbstractEdgeRepositoryTest;
import org.springframework.data.gremlin.object.tests.tinker.core.Tinker_Core_TestConfiguration;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Tinker_Neo4j_TestConfiguration.class)
public class Tinker_Neo4j_EdgeRepositoryTest extends AbstractEdgeRepositoryTest { }
