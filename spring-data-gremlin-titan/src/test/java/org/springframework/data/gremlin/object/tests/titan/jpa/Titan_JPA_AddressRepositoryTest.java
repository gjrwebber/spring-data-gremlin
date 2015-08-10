package org.springframework.data.gremlin.object.tests.titan.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractAddressRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_JPA_TestConfiguration.class)
public class Titan_JPA_AddressRepositoryTest extends AbstractAddressRepositoryTest { }
