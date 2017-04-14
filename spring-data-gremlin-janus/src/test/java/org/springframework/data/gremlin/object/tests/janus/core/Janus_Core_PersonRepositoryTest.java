package org.springframework.data.gremlin.object.tests.janus.core;

import org.springframework.data.gremlin.object.core.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */
@ContextConfiguration(classes = Janus_Core_TestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class Janus_Core_PersonRepositoryTest extends AbstractPersonRepositoryTest {

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
