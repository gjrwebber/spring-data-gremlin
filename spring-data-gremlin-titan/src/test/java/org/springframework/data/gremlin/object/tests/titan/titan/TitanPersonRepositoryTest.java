package org.springframework.data.gremlin.object.tests.titan.titan;

import org.springframework.data.gremlin.object.repository.AbstractPersonRepositoryTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by gman on 24/06/15.
 */
@ContextConfiguration(classes = TitanTestConfiguration.class)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class TitanPersonRepositoryTest extends AbstractPersonRepositoryTest {

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
