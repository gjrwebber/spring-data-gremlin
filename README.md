#Spring Data Gremlin

Spring data gremlin makes it easier to implement Graph based repositories. This module extends [Spring Data](http://projects.spring.io/spring-data) to allow support for potentially any [Graph database](https://en.wikipedia.org/wiki/Graph_database) that implements the [Tinkerpop Blueprints API](https://github.com/tinkerpop/blueprints/wiki).

##Features

- All the great features of [Spring Data](http://projects.spring.io/spring-data)
- Support for [OrientDB](http://orientdb.com) and [TitanDB](http://s3.thinkaurelius.com/docs/titan/current)  out of the box
- Schema building in supported databases
- Sophisticated support to build repositories based on Spring and JPA. ([Frames](http://frames.tinkerpop.com) coming soon)
- Pagination support
- Unique, non-unique and spatial indices supported
- Support for [Gremlin query language](http://gremlin.tinkerpop.com/)
- Support for native queries (Eg. [OrientDB SQL](http://orientdb.com/docs/2.0/orientdb.wiki/SQL-Query.html))
- JavaConfig based repository configuration by introducing @EnableGremlinRepositories
- Mapped and composite query results

##Getting Started

Create your domain objects:

####Person

```
@Entity
public class Person {

    @Id
    private String id;

    @Column(name= "customer_name")
    private String name;

    @OneToOne
    @Column(name = "lives_at")
    private Address address;

    @OneToMany
    @Column(name = "was_located_at")
    private Set<Location> locations;

}
```

####Address

```
@Entity
public class Address {

    @Id
    private String id;

    private String country;

    private String city;

    private String street;
}
```

####Location

```
@Entity
public class Location {

    @Id
    private String id;
    
    private Date date;

    @Index(type = SPATIAL_LATITUDE)
    private double latitude;
    
    @Index(type = SPATIAL_LONGITUDE)
    private double longitude;

}
```

Now create a repository for people:

```
public interface PersonRepository extends GremlinRepository<Person> {

    List<Person> findByLastName(String lastName);

    List<Person> findByLastNameLike(String lastName);

    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findByFirstNameOrLastName(String firstName, String lastName);

    List<Person> findByFirstNameLike(String string);

    @Query(value = "graph.V().has('firstName', ?)")
    List<Person> findByFirstName(String firstName);

    @Query(value = "graph.V().has('firstName', ?)")
    Page<Person> findByFirstName(String firstName, Pageable pageable);

    @Query(value = "graph.V().has('firstName', ?)")
    List<Map<String, Object>> findMapByFirstName(String firstName);

    @Query(value = "graph.V().has('firstName', ?)")
    Map<String, Object> findSingleMapByFirstName(String firstName);

    List<Person> findByAddress_City(String city);

    @Query(value = "delete vertex from (select from Person where firstName <> ?)", nativeQuery = true, modify = true)
    Integer deleteAllExceptPerson(String firstName);

    @Query(value = "select expand(in('was_located_at')) from (select from Location where [latitude,longitude,$spatial] near [?,?,{\"maxDistance\":?}])", nativeQuery = true)
    Page<Person> findNear(double latitude, double longitude, double radius, Pageable pageable);

}

```

##TODO

- Spring auto configuration 
- Index for multiple properties
- Many to many relationships
- Links as entities
- Lazy fetching
- Repository definitions using [Frames](http://frames.tinkerpop.com)
- More [Blueprints](https://github.com/tinkerpop/blueprints/wiki) implementations ([Neo4j](https://en.wikipedia.org/wiki/Neo4j), [ArangoDB](https://www.arangodb.com), [Blazegraph](http://www.blazegraph.com/bigdata), etc.)
- Migrate to [Tinkerpop 3.0](http://www.tinkerpop.com/docs/3.0.0.M1/)