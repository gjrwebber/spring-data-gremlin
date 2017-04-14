package org.springframework.data.gremlin.object.core.repository;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.gremlin.object.core.domain.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;

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
        List<Person> people = repository.findByFirstNameLike("La");
        assertFalse(people.isEmpty());
        for (Person person : people) {
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
    public void noCascadeInLink() {
        Location location = new Location(23, 171);
        location.setArea(new Area("TestArea"));
        location = locationRepository.save(location);

        location = locationRepository.findOne(location.getId());
        notNull(location);
        // Area should not be null as the vertex will be created, but the contents should be empty since properties should not be cascaded.
        notNull(location.getArea());
        isNull(location.getArea().getName());
    }

    @Test
    public void overrideCascadeInLinkWithSystemProperty() {
        System.setProperty("sdg-cascade-all", "true");
        Location location = new Location(23, 171);
        location.setArea(new Area("TestArea"));
        location = locationRepository.save(location);

        location = locationRepository.findOne(location.getId());
        notNull(location);
        notNull(location.getArea());
        assertEquals("TestArea", location.getArea().getName());
        System.setProperty("sdg-cascade-all", "false");
    }

    @Test
    public void overrideCascadeOutLink() {
        Person person = repository.findByAddress_Area_Name("2043").get(0);
        assertEquals("2043", person.getAddress().getArea().getName());
        person.getAddress().getArea().setName("9999");
        repository.save(person, person.getAddress());
        assertEquals(0, repository.findByAddress_Area_Name("9999").size());
        assertEquals(1, repository.findByAddress_Area_Name("2043").size());
    }


    @Test
    public void testLocations() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham);
        assertNotNull(graham.getLocations());
        assertEquals(5, graham.getLocations().size());
        List<Located> locations = new ArrayList<Located>(graham.getLocations());
        Collections.sort(locations, new Comparator<Located>() {
            @Override
            public int compare(Located o1, Located o2) {
                return (int) (o1.getLocation().getLatitude() - o2.getLocation().getLatitude());
            }
        });
        Located location = locations.get(0);
        assertNotNull(location);
        assertEquals(-33, location.getLocation().getLatitude(), 0.00001);
        location = locations.get(1);
        assertNotNull(location);
        assertEquals(-32, location.getLocation().getLatitude(), 0.00001);
    }

    @Test
    public void testCollectionsCascade() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        List<Located> locations = new ArrayList<Located>(graham.getLocations());
        Collections.sort(locations, new Comparator<Located>() {
            @Override
            public int compare(Located o1, Located o2) {
                return (int) (o1.getLocation().getLatitude() - o2.getLocation().getLatitude());
            }
        });
        assertEquals(151, locations.get(0).getLocation().getLongitude(), 0.0001);

        locations.get(0).getLocation().setLongitude(100);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        locations = new ArrayList<Located>(graham.getLocations());
        Collections.sort(locations, new Comparator<Located>() {
            @Override
            public int compare(Located o1, Located o2) {
                return (int) (o1.getLocation().getLatitude() - o2.getLocation().getLatitude());
            }
        });
        assertEquals(100, locations.get(0).getLocation().getLongitude(), 0.0001);

    }


    @Test
    public void testCollectionsCascadeAdd() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        Location location = new Location(-60, 120);
        locationRepository.save(location);
        Located located = new Located(new Date(), graham, location);
        graham.getLocations().add(located);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        List<Located> locations = new ArrayList<Located>(graham.getLocations());
        assertEquals(6, locations.size());

        boolean found = false;
        for (Located latestLocated : locations) {
            if (latestLocated.getLocation().getLongitude() == 120) {
                found = true;
            }
        }
        assertTrue("Did not find a new location with longitude 120", found);
    }

    @Test
    public void testCollectionsCascadeRemove() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(5, graham.getLocations().size());

        List<Located> locations = new ArrayList<Located>(graham.getLocations());
        locations.remove(0);
        graham.setLocations(new HashSet<Located>(locations));
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        locations = new ArrayList<Located>(graham.getLocations());
        assertEquals(4, locations.size());
    }


    @Test
    public void testViaLink() {

        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham);
        Located located = graham.getCurrentLocation();
        assertNotNull(located);
        Person person = located.getPerson();
        Location location = located.getLocation();
        assertNotNull(person);
        assertNotNull(location);
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

    @Test
    public void testEnumCollection() {
        Person graham = repository.findByFirstName("Graham").get(0);

        Set<Person.VEHICLE> vehicles = graham.getWantedVehicles();
        assertNotNull(vehicles);
        assertEquals(2, vehicles.size());

        assertTrue(vehicles.contains(Person.VEHICLE.HOVERCRAFT));
        assertTrue(vehicles.contains(Person.VEHICLE.SPACESHIP));

        vehicles.remove(Person.VEHICLE.HOVERCRAFT);
        vehicles.add(Person.VEHICLE.SKATEBOARD);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        vehicles = graham.getWantedVehicles();
        assertNotNull(vehicles);
        assertEquals(2, vehicles.size());

        assertTrue(vehicles.contains(Person.VEHICLE.SPACESHIP));
        assertTrue(vehicles.contains(Person.VEHICLE.SKATEBOARD));
    }


    @Test
    public void saveSerializable() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham.getOwns());
        assertEquals(3, graham.getOwns().getRooms());
    }

    @Test
    public void saveSerializableCollection() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham.getOwned());
        assertEquals(2, graham.getOwned().size());
        boolean house1 = false;
        boolean house2 = false;
        for (House house : graham.getOwned()) {
            if (house.getRooms() == 1) {
                house1 = true;
            } else if (house.getRooms() == 2) {
                house2 = true;
            }
        }
        assertTrue("House1 was not serialized properly", house1);
        assertTrue("House2 was not serialized properly", house2);
    }

    @Test
    public void saveJson() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham.getFavouritePet());
        assertEquals("Milo", graham.getFavouritePet().getName());
    }

    @Test
    public void saveJsonCollection() {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertNotNull(graham.getPets());
        assertEquals(3, graham.getPets().size());
        boolean milo = false;
        boolean charlie = false;
        boolean toc = false;
        for (Pet pet : graham.getPets()) {
            if (pet.getName().equalsIgnoreCase("Milo") && pet.getType() == Pet.TYPE.DOG) {
                milo = true;
            } else if (pet.getName().equalsIgnoreCase("Charlie") && pet.getType() == Pet.TYPE.CAT) {
                charlie = true;
            } else if (pet.getName().equalsIgnoreCase("TOC") && pet.getType() == Pet.TYPE.CAT) {
                toc = true;
            }
        }
        assertTrue("Milo was not serialized properly", milo);
        assertTrue("Charlie was not serialized properly", charlie);
        assertTrue("TOC was not serialized properly", toc);
    }

    @Test
    public void shouldContainLikes() throws Exception {
        Person graham = repository.findByFirstName("Graham").get(0);
        assertEquals(2, graham.getLikes().size());
    }

    @Test
    public void shouldRemoveLikes() throws Exception {

        // Sanity check
        List<Likes> allLikes = new ArrayList<>();
        CollectionUtils.addAll(allLikes, likesRepository.findAll());
        assertEquals(5, allLikes.size());

        Person graham = repository.findByFirstName("Graham").get(0);
        Likes like = graham.getLikes().iterator().next();
        graham.getLikes().remove(like);
        repository.save(graham);

        graham = repository.findByFirstName("Graham").get(0);

        assertEquals(1, graham.getLikes().size());

        allLikes.clear();
        CollectionUtils.addAll(allLikes, likesRepository.findAll());
        assertEquals(4, allLikes.size());
    }

    @Test
    public void saveDynamicMap() {
        Person person = new Person("Sasa", "Brown");

        Map<String, Object> randoms = new HashMap();
        randoms.put("date", new Date());
        randoms.put("boo", true);
        randoms.put("status", 1);
        randoms.put("hello", null);

        person.setRandoms(randoms);

        String id = repository.save(person).getId();

        Person result = repository.findOne(id);

        assertEquals(result.getFirstName(), person.getFirstName());
        assertEquals(result.getLastName(), person.getLastName());
        assertNotNull(result.getRandoms());
        assertEquals(4, result.getRandoms().size()); // 4 = - hello + _id_
    }


    @Test
    public void saveDynamicMap_and_RemoveOldProperty() {
        Person person = new Person("Sasa", "Brown");

        Map<String, Object> randoms = new HashMap();
        randoms.put("date", new Date());
        randoms.put("boo", true);
        randoms.put("status", 1);
        randoms.put("hello", null);
        person.setRandoms(randoms);

        Map<String, Object> other = new HashMap();
        other.put("hello", "world");
        person.setOtherStuff(other);

        String id = repository.save(person).getId();

        Person result = repository.findOne(id);

        assertNotNull(result.getRandoms());
        assertEquals(4, result.getRandoms().size());
        assertNotNull(result.getOtherStuff());
        assertEquals(2, result.getOtherStuff().size());

        result.getRandoms().remove("status");

        repository.save(result);

        result = repository.findOne(id);

        assertNotNull(result.getRandoms());
        assertEquals(3, result.getRandoms().size());
        assertNotNull(result.getOtherStuff());
        assertEquals(2, result.getOtherStuff().size());
    }
}
