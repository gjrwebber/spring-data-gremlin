package org.springframework.data.gremlin.object.tests.titan.core;

import org.springframework.data.gremlin.object.core.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_Core_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Titan_Core_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
