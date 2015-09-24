package org.springframework.data.gremlin.object.tests.orientdb.core;

import org.springframework.data.gremlin.object.core.repository.AbstractLocationRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDB_Core_TestConfiguration.class)
public class OrientDB_Core_SpatialRepositoryTest extends AbstractLocationRepositoryTest { }
