package org.springframework.data.gremlin.object.tests.tinker;

import org.springframework.data.gremlin.object.jpa.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = TinkerTestConfiguration.class)
public class TinkerPersonRepositoryTest extends AbstractPersonRepositoryTest { }
