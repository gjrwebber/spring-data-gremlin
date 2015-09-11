package org.springframework.data.gremlin.object.jpa.repository;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.gremlin.object.jpa.domain.Location;
import org.springframework.data.gremlin.object.jpa.domain.Person;

import java.util.*;

import static org.junit.Assert.*;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractPersonRepositoryTest extends BaseRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPersonRepositoryTest.class);

    @Test
    public void savePerson() {
        Person person = new Person("Sasa", "Brown");
        String id = repository.save(person).getId();

        Person result = repository.findOne(id);

        assertEquals(result.getFirstName(), person.getFirstName());
        assertEquals(result.getLastName(), person.getLastName());
    }

    @Test
    public void countPerson() {
        assertEquals(5, repository.count());
    }

    @Test
    public void findAllPersons() {
        List<Person> persons = Lists.newArrayList(repository.findAll());
        assertNotNull(persons);
        assertEquals(5, persons.size());
    }

    @Test
    public void findAllPersonsPageable() {
        List<Person> persons = Lists.newArrayList(repository.findAll());
        assertNotNull(persons);
        assertEquals(5, persons.size());

        Page<Person> result = repository.findAll(new PageRequest(0, 2));

        assertTrue(result.hasContent());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getNumberOfElements());

        result = repository.findAll(new PageRequest(1, 2));

        assertTrue(result.hasContent());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getNumberOfElements());

        result = repository.findAll(new PageRequest(2, 2));

        assertTrue(result.hasContent());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getNumberOfElements());
    }

    @Test
    public void countByFirstName() {
        assertEquals(repository.countByFirstName("Vanja"), Long.valueOf(1));
    }

    @Test
    public void countByLastName() {
        assertEquals(repository.countByLastName("Webber"), Long.valueOf(2));
    }

    @Test
    public void findByLastName() {
        List<Person> result = repository.findByLastName("Webber");

        assertFalse(result.isEmpty());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Webber");
        }
    }

    @Test
    public void findByLastNamePageable() {
        Page<Person> result = repository.findByLastName("Ivanovic", new PageRequest(0, 2));

        assertTrue(result.hasContent());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.getNumberOfElements());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Ivanovic");
        }


        result = repository.findByLastName("Ivanovic", new PageRequest(1, 2));

        assertTrue(result.hasContent());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getNumberOfElements());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Ivanovic");
        }

    }

    @Test
    public void queryLastNamePageable() {
        Page<Person> result = repository.queryLastName("Ivanovic", new PageRequest(0, 2));

        assertTrue(result.hasContent());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(2, result.getNumberOfElements());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Ivanovic");
        }


        result = repository.queryLastName("Ivanovic", new PageRequest(1, 2));

        assertTrue(result.hasContent());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(1, result.getNumberOfElements());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Ivanovic");
        }

    }

    @Test
    public void findByFirstName() {
        List<Person> result = repository.findByFirstName("Jake");

        assertEquals(1, result.size());

        for (Person person : result) {
            assertEquals(person.getFirstName(), "Jake");
        }
    }

    @Test
    public void findByFirstNameWithParam() {
        List<Person> result = repository.findByFirstNameWithParam("Jake");

        assertEquals(1, result.size());

        for (Person person : result) {
            assertEquals(person.getFirstName(), "Jake");
        }
    }

    @Test
    public void findMapByFirstName() {
        List<Map<String, Object>> result = repository.findMapByFirstName("Jake");

        assertFalse(result.isEmpty());

        for (Map<String, Object> personMap : result) {
            assertNotNull(personMap.get("firstName"));
            assertEquals("Jake", personMap.get("firstName"));
        }
    }

    @Test
    public void findSingleByFirstName() {
        Person person = repository.findSingleByFirstName("Jake");

        assertNotNull(person);

        assertEquals(person.getFirstName(), "Jake");
    }

    @Test
    public void findSingleMapByFirstName() {
        Map<String, Object> result = repository.findSingleMapByFirstName("Jake");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertNotNull(result.get("firstName"));
        assertEquals("Jake", result.get("firstName"));
    }

    @Test
    public void findByFirstNameLike() {
        for (Person person : repository.findByFirstNameLike("La")) {
            assertTrue(person.getFirstName().startsWith("La"));
        }
    }

    @Test
    public void findByLastNameLike() {
        List<Person> persons = repository.findByLastNameLike("We");
        assertEquals(2, persons.size());
        for (Person person : persons) {
            assertTrue(person.getLastName().startsWith("We"));
        }
    }

    @Test
    public void findByFirstNameAndLastName() {
        List<Person> persons = repository.findByFirstNameAndLastName("Vanja", "Ivanovic");
        assertEquals(1, persons.size());
        for (Person person : persons) {
            assertTrue(person.getFirstName().equals("Vanja") && person.getLastName().equals("Ivanovic"));
        }
    }

    @Test
    public void findByFirstNameOrLastName() {
        List<Person> persons = repository.findByFirstNameOrLastName("Graham", "Ivanovic");
        assertEquals(4, persons.size());
        for (Person person : persons) {
            assertTrue(person.getFirstName().equals("Graham") || person.getLastName().equals("Ivanovic"));
        }
    }

    @Test
    public void findByActiveIsTrue() {
        List<Person> persons = repository.findByActiveIsTrue();
        assertFalse(persons.isEmpty());
        for (Person person : persons) {
            assertTrue(person.getActive());
        }
    }

    @Test
    public void findByActiveIsFalse() {
        List<Person> persons = repository.findByActiveIsFalse();
        assertFalse(persons.isEmpty());
        for (Person person : persons) {
            assertFalse(person.getActive());
        }
    }

    @Test
    public void findByCityTest() {
        List<Person> persons = repository.findByAddress_City("Newcastle");
        assertEquals(4, persons.size());
        for (Person person : persons) {
            assertEquals(person.getAddress().getCity(), "Newcastle");
        }
    }

    @Test
    public void findByLastNameOrCityTest() {
        List<Person> persons = repository.findByLastNameOrAddress_City("Ivanovic", "Newcastle");
        assertEquals(5, persons.size());
        for (Person person : persons) {
            assertTrue(person.getAddress().getCity().equals("Newcastle") || person.getLastName().equals("Ivanovic"));
        }
    }

    @Test
    public void findBy2LevelReferenceTest() {
        List<Person> persons = repository.findByAddress_Area_Name("2043");
        assertEquals(1, persons.size());
        for (Person person : persons) {
            assertTrue(person.getAddress().getArea().getName().equals("2043"));
        }
    }

    @Test
    public void findByLastNameOrPostcodeTest() {
        List<Person> persons = repository.findByLastNameOrAddress_Area_Name("Ivanovic", "2291");
        assertEquals(5, persons.size());
        for (Person person : persons) {
            assertTrue(person.getAddress().getArea().getName().equals("2291") || person.getLastName().equals("Ivanovic"));
        }
    }


    @Test
    public void saveAddressCascade() {
        Person person = repository.findByAddress_Area_Name("2043").get(0);
        assertEquals("Sydney", person.getAddress().getCity());
        person.getAddress().setCity("Woo");
        repository.save(person);
        person = repository.findByAddress_Area_Name("2043").get(0);
        assertEquals("Woo", person.getAddress().getCity());
    }

    @Test
    public void saveArea2LevelCascade() {
        Person person = repository.findByAddress_Area_Name("2043").get(0);
        assertEquals("2043", person.getAddress().getArea().getName());
        person.getAddress().getArea().setName("9999");
        repository.save(person);
        assertEquals(0, repository.findByAddress_Area_Name("2043").size());
        person = repository.findByAddress_Area_Name("9999").get(0);
        assertEquals("9999", person.getAddress().getArea().getName());
    }

    @Test
    public void testLocations() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham);
        assertNotNull(graham.getLocations());
        assertEquals(5, graham.getLocations().size());
        List<Location> locations = new ArrayList<Location>(graham.getLocations());
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return (int) Math.round(o1.getLatitude() - o2.getLatitude());
            }
        });
        Location location = locations.get(0);
        assertNotNull(location);
        assertEquals(-33, location.getLatitude(), 0.00001);
        location = locations.get(1);
        assertNotNull(location);
        assertEquals(-32, location.getLatitude(), 0.00001);
    }

    @Test
    public void testCollectionsCascade() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        List<Location> locations = new ArrayList<Location>(graham.getLocations());
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return (int) Math.round(o1.getLatitude() - o2.getLatitude());
            }
        });
        assertEquals(151, locations.get(0).getLongitude(), 0.0001);

        locations.get(0).setLongitude(100);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        locations = new ArrayList<Location>(graham.getLocations());
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return (int) Math.round(o1.getLatitude() - o2.getLatitude());
            }
        });
        assertEquals(100, locations.get(0).getLongitude(), 0.0001);

    }


    @Test
    public void testCollectionsCascadeAdd() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        Location location = new Location(-60, 120);
        locationRepository.save(location);
        graham.getLocations().add(location);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        List<Location> locations = new ArrayList<Location>(graham.getLocations());
        assertEquals(6, locations.size());
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return (int) Math.round(o1.getLatitude() - o2.getLatitude());
            }
        });
        assertEquals(120, locations.get(0).getLongitude(), 0.0001);

    }

    @Test
    public void testCollectionsCascadeRemove() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        List<Location> locations = new ArrayList<Location>(graham.getLocations());
        locations.remove(0);
        graham.setLocations(new HashSet<Location>(locations));
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        locations = new ArrayList<Location>(graham.getLocations());
        assertEquals(4, locations.size());
    }


    @Test
    public void testEnum() {
        Person graham = repository.findByFirstName("Graham").get(0);

        assertEquals(Person.AWESOME.YES, graham.getAwesome());

        graham.setAwesome(Person.AWESOME.NO);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);
        assertEquals(Person.AWESOME.NO, graham.getAwesome());
    }

    @Test
    public void testEnumCollectionConcreteType() {
        Person graham = repository.findByFirstName("Graham").get(0);

        Set<Person.VEHICLE> vehicles = graham.getVehicles();
        assertNotNull(vehicles);
        assertEquals(2, vehicles.size());

        assertTrue(vehicles.contains(Person.VEHICLE.CAR));
        assertTrue(vehicles.contains(Person.VEHICLE.MOTORBIKE));

        vehicles.remove(Person.VEHICLE.CAR);
        vehicles.add(Person.VEHICLE.SKATEBOARD);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        vehicles = graham.getVehicles();
        assertNotNull(vehicles);
        assertEquals(2, vehicles.size());

        assertTrue(vehicles.contains(Person.VEHICLE.MOTORBIKE));
        assertTrue(vehicles.contains(Person.VEHICLE.SKATEBOARD));
    }

}
