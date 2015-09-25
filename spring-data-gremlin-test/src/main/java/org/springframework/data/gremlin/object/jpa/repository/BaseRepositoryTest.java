package org.springframework.data.gremlin.object.jpa.repository;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
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
import org.springframework.data.gremlin.object.jpa.TestService;
import org.springframework.data.gremlin.object.jpa.domain.*;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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

        Set<Location> locations = new HashSet<Location>();
        for (int i = 0; i < 5; i++) {
            Location location = new Location(-33 + i, 151 + i);
            locationRepository.save(location);
            locations.add(location);
        }

        Person graham = new Person("Graham", "Webber", address, true);
        graham.setLocations(locations);
        graham.addVehicle(Person.VEHICLE.CAR);
        graham.addVehicle(Person.VEHICLE.MOTORBIKE);

        graham.setOwns(new House(3));
        graham.getOwned().add(new House(1));
        graham.getOwned().add(new House(2));
        Pet milo = new Pet("Milo", Pet.TYPE.DOG);
        graham.getPets().add(milo);
        graham.getPets().add(new Pet("Charlie", Pet.TYPE.CAT));
        graham.getPets().add(new Pet("TOC", Pet.TYPE.CAT));

        graham.setFavouritePet(milo);


        repository.save(graham);
        repository.save(new Person("Vanja", "Ivanovic", address, true));
        repository.save(new Person("Lara", "Ivanovic", address, true));
        repository.save(new Person("Jake", "Webber", address, false));
        repository.save(new Person("Sandra", "Ivanovic", new Address(new Country("Australia"), "Sydney", "Wilson St", new Area("2043")), false));


        List<Vertex> addresses = graph.traversal().V().has("street").toList();
        assertNotNull(addresses);
        for (Vertex addr : addresses) {
            assertNotNull(addr);
            assertTrue(addr.property("street").equals("Wilson St") || addr.property("street").equals("Scenic Dr"));
        }

        ScriptEngine engine = new GremlinGroovyScriptEngine();

        Bindings bindings = engine.createBindings();
        bindings.put("g", graph);
        bindings.put("firstName", "Jake");

        try {
            GraphTraversal obj = (GraphTraversal) engine.eval("g.V().has('firstName', firstName)", bindings);
            assertTrue(obj.hasNext());
            Object o = obj.next();
            assertNotNull(o);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        GraphTraversalSource source = GraphTraversalSource.build().create(graph);
        GraphTraversal<Vertex, Vertex> pipe = source.V().or(source.V().has("firstName", "Jake"), source.V().has("firstName", "Graham"));

        assertTrue("No Jake or Graham in Pipe!", pipe.hasNext());
        while (pipe.hasNext()) {
            Vertex obj = pipe.next();
            assertNotNull(obj);
            assertTrue(obj.property("firstName").equals("Graham") || obj.property("firstName").equals("Jake"));
        }

        GraphTraversal<Vertex, Vertex> linkedPipe = source.V().outE("lives_at").inV().has("city", "Newcastle");

        assertTrue("No lives_at in Pipe!", linkedPipe.hasNext());
        while (linkedPipe.hasNext()) {
            Vertex obj = linkedPipe.next();
            assertNotNull(obj);
            assertTrue(obj.property("city").equals("Newcastle"));
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
