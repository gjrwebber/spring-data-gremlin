package org.springframework.data.gremlin.object.tests.titan.core;

import org.springframework.data.gremlin.object.core.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = Titan_Core_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Titan_Core_PersonRepositoryTest extends AbstractPersonRepositoryTest {

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
