package org.springframework.data.gremlin.object.tests.janus.core;

import org.springframework.data.gremlin.object.core.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */
@ContextConfiguration(classes = Janus_Core_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Janus_Core_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
