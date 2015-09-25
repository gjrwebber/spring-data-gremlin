package org.springframework.data.gremlin.object.tests.orientdb.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractAddressRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDB_JPA_TestConfiguration.class)
public class OrientDB_JPA_AddressRepositoryTest extends AbstractAddressRepositoryTest { }
