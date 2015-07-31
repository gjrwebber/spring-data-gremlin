package org.springframework.data.gremlin.object.tests.orientdb;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.gremlin.object.domain.Person;
import org.springframework.data.gremlin.object.repository.AbstractPersonRepositoryTest;
import org.springframework.data.gremlin.object.repository.AbstractTransactionRepositoryTest;
import org.springframework.data.gremlin.object.repository.NativePersonRepository;
import org.springframework.test.context.ContextConfiguration;

import java.util.Iterator;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDBTestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrientDBTransactionRepositoryTest extends AbstractTransactionRepositoryTest {

}
