#Spring Data Gremlin

Spring data gremlin makes it easier to implement Graph based repositories. This module extends [Spring Data](http://projects.spring.io/spring-data) to allow support for potentially any [Graph database](https://en.wikipedia.org/wiki/Graph_database) that implements the [Tinkerpop Blueprints 2.x API](https://github.com/tinkerpop/blueprints/wiki). 

##Features

- All the great features of [Spring Data](http://projects.spring.io/spring-data)
- Support for [OrientDB](http://orientdb.com) and [TitanDB](http://s3.thinkaurelius.com/docs/titan/current)  out of the box
- Schema creation in supported databases
- Support to build repositories based on Spring using our [custom set of annotations](https://github.com/gjrwebber/org/springframework/data/gremlin/annotation), [spring-data-neo4j](http://docs.spring.io/spring-data/neo4j/docs/current/reference/html/#reference_programming-model_annotations) or JPA annotations.
- Vertex and Edge repository support
- Pagination support
- Unique, non-unique and spatial indices supported
- Support for [Gremlin query language](http://gremlin.tinkerpop.com/) through the ```@Query``` annotation
- Support for native queries (Eg. [OrientDB SQL](http://orientdb.com/docs/2.0/orientdb.wiki/SQL-Query.html)) through the ```@Query``` annotation
- JavaConfig based repository configuration by introducing @EnableGremlinRepositories
- ```Map``` and ```CompositeResult``` query result objects


##Default Schema Generation

Below is a list of default annotations used by the ```DefaultSchemaGenerator```.


- ```@Vertex``` maps an ```Object``` to a ```Vertex```
- ```@Edge``` maps an ```Object``` to an ```Edge```
- ```@Embeddable``` maps an ```Object``` to set of properties to be embedded in a "parent" vertex
- ```@Id``` maps an instance variable to the vertex or edge ID
- ```@Index``` used for indexing properties including unique, spatial and non-unique
- ```@Property``` maps an instance variable to a vertex property (optional, only required if you want to name it differently)
- ```@Embed``` embeds the referenced ```Object``` in the "parent" vertex
- ```@PropertyOverride``` can be used within ```@Embed``` for overriding properties within ```@Embeddable```s. 
- ```@Ignore``` ignores a variable
- ```@Enumerated``` allows for mapping an enum as an ordinal, otherwise String is the default mapping
- ```@Link``` creates a link from this vertex to the referenced ```Object```'s vertex or ```Collection```'s verticies using the name of the field as default or the optional ```type``` parameter as the link label
- ```@LinkVia``` creates an ```Edge``` based the the referenced ```Object``` or ```Collection``` which must be a ```@Edge```
- ```@FromVertex``` defines the starting (or OUT) vertex of a ```@Edge```
- ```@ToVertex``` defines the ending (or IN) vertex of a ```@Edge```


##Neo4j Schema Generation

Below is a list of supported annotations used by the ```Neo4jSchemaGenerator```. These annotations are part of the [spring-data-neo4j](http://docs.spring.io/spring-data/neo4j/docs/current/reference/html/#reference_programming-model_annotations) platform.


- ```@NodeEntity``` maps an ```Object``` to a ```Vertex```
- ```@RelationshipEntity``` maps an ```Object``` to an ```Edge```
- ```@GraphId``` maps an instance variable to the vertex or edge ID
- ```@Indexed``` used for indexing properties
- ```@GraphProperty``` maps an instance variable to a vertex property (optional, only required if you want to name it differently)
- ```@RelatedTo``` creates a link from this vertex to the referenced ```Object```'s vertex or ```Collection```'s verticies using the name of the field as default or the optional ```type``` parameter as the link label
- ```@RelatedToVia``` creates an ```Edge``` based the the referenced ```Object``` or ```Collection``` which must be a ```@RelationshipEntity```
- ```@StartNode``` defines the starting (or OUT) vertex of a ```@RelationshipEntity```
- ```@EndNode``` defines the ending (or IN) vertex of a ```@RelationshipEntity```

##JPA Schema Generation

Below is a list of supported annotations used by the ```JpaSchemaGenerator```:

- ```@Entity``` maps an ```Object``` to a ```Vertex```
- ```@Embeddable``` maps an ```Object``` to set of properties to be embedded in a "parent" vertex
- ```@Id``` maps an instance variable to the vertex ID
- ```@Column``` maps an instance variable to a vertex property
- ```@Embedded``` embeds the referenced ```Object``` in the "parent" vertex
- ```@AttributeOverrides``` and ```@AttributeOverride``` can be used for overriding ```@Embedded``` property names.   
- ```@OneToOne``` creates an outgoing link from this vertex to the referenced ```Object```'s vertex using the name of the field as default or the optional ```@Column```'s name field as the link label
- ```@OneToMany``` creates an outgoing link from this vertex to all of the referenced ```Collection```'s vertices using the name of the field as default or the optional ```@Column```'s name field as the link label
- ```@Transient``` marks an instance variable as transient
- ```@Enumerated``` allows for mapping an enum as a ordinal, otherwise String is the default mapping

##Getting Started

###Download dependencies

Currenlty only SNAPSHOT builds are being uploaded to sonatype so you will need to add ```https://oss.sonatype.org/content/repositories/snapshots/``` repository URL to your build configuration.

####Maven
```
<repositories>
    <repository>
        <id>spring.data.gremlin.snapshot</id>
        <name>Spring Data Gremlin SNAPHSHOT</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

####Gradle
```
repositories {
    //...
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}
```

Once you have your build configuration setup you need to add the correct dependencies. To do that you need to decide which database and schema generator you want to use. If you are starting from scratch, then the default schema generator is for you.

####Database dependency
**OrientDB** - com.github.gjrwebber:spring-data-gremlin-orientdb:0.1.0-SNAPSHOT  
**TitanDB** - com.github.gjrwebber:spring-data-gremlin-titan:0.1.0-SNAPSHOT

####Schema generator dependency
**Default** - No further dependency  
**JPA** - com.github.gjrwebber:spring-data-gremlin-schemagen-jpa:0.1.0-SNAPSHOT  
**Neo4j** - com.github.gjrwebber:spring-data-gremlin-schemagen-neo4j:0.1.0-SNAPSHOT

####Maven example

Using OrientDB database with Neo4j schema generator:

```
<dependency>
    <groupId>com.github.gjrwebber</groupId>
    <artifactId>spring-data-gremlin-orientdb</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.github.gjrwebber</groupId>
    <artifactId>spring-data-gremlin-schemagen-neo4j</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

####Gradle example

Using TitanDB with default schema generator:

```
compile("com.github.gjrwebber:spring-data-gremlin-titan:0.1.0-SNAPSHOT")
```

###Create you domain model

Create your domain objects. I have used the default generator for mapping the schema, but you can also use [spring-data-neo4j](http://docs.spring.io/spring-data/neo4j/docs/current/reference/html/#reference_programming-model_annotations) or JPA annotations if you wish:

####Person

```
@Vertex
public class Person {

    @Id
    private String id;

    @Property("customer_name")
    private String name;

    @Link("lives_at")
    private Address address;

    @LinkVia
    private Set<Located> locations;

    @LinkVia
    private Located currentLocation;


}
```

####Address

```
@Vertex
public class Address {

    @Id
    private String id;

    @Embed(propertyOverrides = { @PropertyOverride(name = "name", property = @Property("countryName")) })
    private Country country;

    private String city;

    private String street;
}
```

####Country (embedded)

```
@Embeddable
public class Country {
	private String name;
}
```

####Location

```
@Vertex
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

####Located
```
@Edge("was_located")
public class Located {

    @Id
    private String id;

    @Property("location_date")
    private Date date;

    @FromVertex
    private Person person;

    @ToVertex
    private Location location;
    
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

And one for Located:

```
public interface LocatedRepository extends GremlinRepository<Located> {

    @Query(value = "graph.V().has('firstName', ?).outE('Location')")
    List<Located> findAllLocatedForUser(String name);
}

```

Wire it up:

```

@Configuration
@EnableTransactionManagement
@EnableGremlinRepositories(basePackages = "test.repos", repositoryFactoryBeanClass = GremlinRepositoryFactoryBean.class)
public class Configuration {

    @Bean
    public OrientDBGremlinGraphFactory orientDBGraphFactory() {
        OrientDBGremlinGraphFactory factory = new OrientDBGremlinGraphFactory();
        factory.setUrl("memory:spring-data-orientdb-db");
        factory.setUsername("admin");
        factory.setPassword("admin");
        return factory;
    }

    @Bean
    public GremlinTransactionManager transactionManager() {
        return new GremlinTransactionManager(orientDBGraphFactory());
    }

    @Bean
    public GremlinSchemaFactory schemaFactory() {
        return new GremlinSchemaFactory();
    }

    @Bean
    public SchemaGenerator schemaGenerator() {
        return new DefaultSchemaGenerator(new OrientDbIdEncoder());
    }

    @Bean
    public SchemaWriter schemaWriter() {
        return new OrientDbSchemaWriter();
    }

    @Bean
    public static GremlinBeanPostProcessor gremlinSchemaManager(SchemaGenerator schemaGenerator) {
        return new GremlinBeanPostProcessor(schemaGenerator, "test.domain");
    }

    @Bean
    public GremlinGraphAdapter graphAdapter() {
        return new OrientDBGraphAdapter();
    }

    @Bean
    public GremlinRepositoryContext databaseContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter) {
        return new GremlinRepositoryContext(graphFactory, graphAdapter, schemaFactory, schemaWriter, OrientDBGremlinRepository.class, NativeOrientdbGremlinQuery.class);
    }
}
```

##TODO

- Spring auto configuration 
- ~~Many to many relationships~~
- ~~Links as entities~~
- ~~Edge repositories~~
- Lazy fetching
- Index for multiple properties
- Allow for IDs other than String
- Repository definitions using ~~[Neo4j](http://docs.spring.io/spring-data/neo4j/docs/current/reference/html/#reference_programming-model_annotations)~~, [Frames](http://frames.tinkerpop.com) or ~~some other custom implementation~~.
- More [Blueprints](https://github.com/tinkerpop/blueprints/wiki) implementations ([Neo4j](https://en.wikipedia.org/wiki/Neo4j), [ArangoDB](https://www.arangodb.com), [Blazegraph](http://www.blazegraph.com/bigdata), etc.)
- Migrate to [Tinkerpop 3.0](http://www.tinkerpop.com/docs/3.0.0.M1/)

##Acknowledgement
This project would not have been possible without the hard work done by the [spring-data-orientdb](https://github.com/orientechnologies/spring-data-orientdb) team. A lot of code and concepts were reused and reshaped. Thanks.

