package org.springframework.data.gremlin.object.tests.orientdb.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDB_JPA_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrientDB_JPA_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
