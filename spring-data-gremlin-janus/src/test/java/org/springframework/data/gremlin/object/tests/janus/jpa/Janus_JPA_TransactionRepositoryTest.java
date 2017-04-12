package org.springframework.data.gremlin.object.tests.janus.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */

@ContextConfiguration(classes = Janus_JPA_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Janus_JPA_TransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
