package org.springframework.data.gremlin.object.tests.orientdb.neo4j;

import org.springframework.data.gremlin.object.neo4j.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDB_Neo4j_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrientDB_Neo4j_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
