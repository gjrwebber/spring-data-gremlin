package org.springframework.data.gremlin.object.tests.orientdb;

import org.springframework.data.gremlin.object.repository.AbstractLocationRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDBTestConfiguration.class)
public class OrientDBSpatialRepositoryTest extends AbstractLocationRepositoryTest { }
