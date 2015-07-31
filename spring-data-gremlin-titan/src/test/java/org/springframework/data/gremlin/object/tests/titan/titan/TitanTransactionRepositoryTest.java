package org.springframework.data.gremlin.object.tests.titan.titan;

import org.springframework.data.gremlin.object.repository.AbstractTransactionRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = TitanTestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class TitanTransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
