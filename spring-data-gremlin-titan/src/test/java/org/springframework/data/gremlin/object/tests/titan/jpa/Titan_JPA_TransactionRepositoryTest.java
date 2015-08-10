package org.springframework.data.gremlin.object.tests.titan.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_JPA_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Titan_JPA_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
