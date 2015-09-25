package org.springframework.data.gremlin.object.tests.titan.jpa;

import org.springframework.data.gremlin.object.jpa.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_JPA_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Titan_JPA_PersonRepositoryTest extends AbstractPersonRepositoryTest {

    //    @Autowired
    //    protected NativePersonRepository nativePersonRepository;

    //    @Test
    //    public void testDeleteAllExcept() throws Exception {
    //        int count = ((NativePersonRepository)repository).deleteAllExceptUser("Lara");
    //        assertEquals(4, count);
    //
    //        Iterable<Person> persons = repository.findAll();
    //        assertNotNull(persons);
    //        Iterator<Person> iterator = persons.iterator();
    //        assertNotNull(iterator);
    //        assertNotNull(iterator.next());
    //        assertFalse(iterator.hasNext());
    //    }
}
