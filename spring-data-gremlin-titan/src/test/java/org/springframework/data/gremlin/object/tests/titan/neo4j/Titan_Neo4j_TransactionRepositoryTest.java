package org.springframework.data.gremlin.object.tests.titan.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_Neo4j_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Titan_Neo4j_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
