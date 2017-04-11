package org.springframework.data.gremlin.object.core.repository;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.core.TestService;
import org.springframework.data.gremlin.object.core.domain.*;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.apache.tinkerpop.gremlin.process.traversal.P.within;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(
        inheritListeners = false,
        listeners = { DependencyInjectionTestExecutionListener.class })
@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class BaseRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseRepositoryTest.class);

    @Autowired
    protected PersonRepository repository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected LocationRepository locationRepository;

    @Autowired
    protected GremlinGraphFactory factory;

    @Autowired
    protected TestService testService;

    @Autowired
    protected LikesRepository likesRepository;

    protected Person graham;

    protected Person lara;

    @Before
    public void before() {

        Graph graph = factory.graph();
        factory.beginTx(graph);
        graph.vertices().forEachRemaining(new Consumer<Vertex>() {
            @Override
            public void accept(Vertex vertex) {
                vertex.remove();
            }
        });


        graph.edges().forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                edge.remove();
            }
        });
        factory.commitTx(graph);

        Address address = new Address(new Country("Australia"), "Newcastle", "Scenic Dr", new Area("2291"));
        addressRepository.save(address);

        graham = new Person("Graham", "Webber", address, true);
        graham.addVehicle(Person.VEHICLE.CAR);
        graham.addVehicle(Person.VEHICLE.MOTORBIKE);
        graham.addWantedVehicle(Person.VEHICLE.HOVERCRAFT);
        graham.addWantedVehicle(Person.VEHICLE.SPACESHIP);

        graham.setOwns(new House(3));
        graham.getOwned().add(new House(1));
        graham.getOwned().add(new House(2));
        Pet milo = new Pet("Milo", Pet.TYPE.DOG);
        graham.getPets().add(milo);
        graham.getPets().add(new Pet("Charlie", Pet.TYPE.CAT));
        graham.getPets().add(new Pet("TOC", Pet.TYPE.CAT));

        graham.setFavouritePet(milo);

        Set<Located> locations = new HashSet<Located>();
        for (int i = 0; i < 5; i++) {
            Location location = new Location(-33 + i, 151 + i);
            locationRepository.save(location);
            Located located = new Located(new Date(), graham, location);
            locations.add(located);
        }

        lara = new Person("Lara", "Ivanovic", address, true);
        graham.setLocations(locations);
        graham.setCurrentLocation(locations.iterator().next());
        repository.save(graham);
        repository.save(new Person("Vanja", "Ivanovic", address, true));
        repository.save(lara);
        repository.save(new Person("Jake", "Webber", address, false));
        repository.save(new Person("Sandra", "Ivanovic", new Address(new Country("Australia"), "Sydney", "Wilson St", new Area("2043")), false));

        Likes like = new Likes(graham, lara);
        likesRepository.save(like);

        List<Vertex> addresses = graph.traversal().V().has("street").toList();
        assertNotNull(addresses);
        for (Vertex addr : addresses) {
            assertNotNull(addr);
            String street = addr.value("street").toString();
            assertTrue(street.equals("Wilson St") || street.equals("Scenic Dr"));
        }

        ScriptEngine engine = new GremlinGroovyScriptEngine();

        Bindings bindings = engine.createBindings();
        bindings.put("g", graph.traversal());
        bindings.put("firstName", "Jake");

        try {
            GraphTraversal obj = (GraphTraversal) engine.eval("g.V().has('firstName', firstName)", bindings);
            assertTrue(obj.hasNext());
            Object o = obj.next();
            assertNotNull(o);
        } catch (ScriptException e) {
            e.printStackTrace();
        }



        GraphTraversalSource source = graph.traversal();

        GraphTraversal<Vertex, Vertex> pipe = source.V().has("firstName", within("Jake", "Graham"));

        assertTrue("No Jake or Graham in Pipe!", pipe.hasNext());
        while (pipe.hasNext()) {
            Vertex obj = pipe.next();
            assertNotNull(obj);
            String firstName = obj.value("firstName").toString();
            assertTrue(firstName.equals("Graham") || firstName.equals("Jake"));
        }


        GraphTraversal<Vertex, Vertex> linkedPipe = source.V().outE("lives_at").inV().has("city", "Newcastle");

        assertTrue("No lives_at in Pipe!", linkedPipe.hasNext());
        while (linkedPipe.hasNext()) {
            Vertex obj = linkedPipe.next();
            assertNotNull(obj);
            assertTrue(obj.value("city").toString().equals("Newcastle"));
        }

        GraphTraversal<Vertex, Edge> likesPipe = source.V().has("firstName", "Lara").inE("Likes");

        assertTrue("No likes in Pipe!", likesPipe.hasNext());
        while (likesPipe.hasNext()) {
            Edge edge = likesPipe.next();
            assertNotNull(edge);
            Vertex v = edge.outVertex();
            assertTrue(v.value("firstName").toString().equals("Graham"));
        }

        factory.commitTx(graph);
    }

    @Test
    public void should_autowire_repos() {
        assertNotNull(repository);
        assertNotNull(addressRepository);
        assertNotNull(locationRepository);
    }

}
