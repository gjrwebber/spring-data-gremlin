package org.springframework.data.gremlin.object.tests.tinker.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Tinker_JPA_TestConfiguration.class)
public class Tinker_JPA_PersonRepositoryTest extends AbstractPersonRepositoryTest { }
