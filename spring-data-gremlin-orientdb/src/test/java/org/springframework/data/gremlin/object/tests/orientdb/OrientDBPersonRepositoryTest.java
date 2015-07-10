package org.springframework.data.gremlin.object.tests.orientdb;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.domain.Person;
import org.springframework.data.gremlin.object.repository.AbstractPersonRepositoryTest;
import org.springframework.data.gremlin.object.repository.NativePersonRepository;
import org.springframework.test.context.ContextConfiguration;

import java.util.Iterator;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = OrientDBTestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class OrientDBPersonRepositoryTest extends AbstractPersonRepositoryTest {

    @Autowired
    protected NativePersonRepository nativePersonRepository;

    @Test
    public void testDeleteAllExcept() throws Exception {
        int count = nativePersonRepository.deleteAllExceptUser("Lara");
        Assert.assertEquals(4, count);

        Iterable<Person> persons = repository.findAll();
        Assert.assertNotNull(persons);
        Iterator<Person> iterator = persons.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertNotNull(iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }
}
