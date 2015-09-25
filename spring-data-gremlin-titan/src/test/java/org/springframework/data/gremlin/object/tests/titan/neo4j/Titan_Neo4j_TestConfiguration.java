package org.springframework.data.gremlin.object.tests.titan.neo4j;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gremlin.config.EnableGremlinRepositories;
import org.springframework.data.gremlin.object.neo4j.TestService;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.gremlin.repository.titan.TitanGraphAdapter;
import org.springframework.data.gremlin.repository.titan.TitanGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.generator.Neo4jSchemaGenerator;
import org.springframework.data.gremlin.schema.generator.SchemaGenerator;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.schema.writer.titan.TitanSchemaWriter;
import org.springframework.data.gremlin.support.GremlinRepositoryFactoryBean;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.GremlinTransactionManager;
import org.springframework.data.gremlin.tx.titan.TitanGremlinGraphFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

@Configuration
@EnableTransactionManagement
@EnableGremlinRepositories(basePackages = "org.springframework.data.gremlin.object.neo4j", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GremlinRepositoryWithNativeSupport.class) }, repositoryFactoryBeanClass = GremlinRepositoryFactoryBean.class)
public class Titan_Neo4j_TestConfiguration {

    @Bean
    public TitanGremlinGraphFactory factory() {
        try {
            FileUtils.forceDeleteOnExit(new File("/tmp/graph"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        TitanGremlinGraphFactory factory = new TitanGremlinGraphFactory();
        factory.setUrl("inmemory");
        //        factory.setUsername("admin");
        //        factory.setPassword("admin");

        return factory;
    }

    @Bean
    public GremlinSchemaFactory mapperFactory() {
        return new GremlinSchemaFactory();
    }

    @Bean
    public GremlinTransactionManager transactionManager() {
        return new GremlinTransactionManager(factory());
    }

    @Bean
    public SchemaGenerator schemaGenerator() {
        return new Neo4jSchemaGenerator();
    }

    @Bean
    public SchemaWriter schemaWriter() {
        return new TitanSchemaWriter();
    }

    @Bean
    public GremlinGraphAdapter graphAdapter() {
        return new TitanGraphAdapter();
    }

    @Bean
    public static GremlinBeanPostProcessor tinkerpopSchemaManager(SchemaGenerator schemaGenerator) {
        return new GremlinBeanPostProcessor(schemaGenerator, "org.springframework.data.gremlin.object.neo4j.domain");
    }

    @Bean
    public GremlinRepositoryContext databaseContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter) {
        return new GremlinRepositoryContext(graphFactory, graphAdapter, schemaFactory, schemaWriter, TitanGremlinRepository.class);
    }

    @Bean
    public TestService testService() {
        return new TestService();
    }
}
