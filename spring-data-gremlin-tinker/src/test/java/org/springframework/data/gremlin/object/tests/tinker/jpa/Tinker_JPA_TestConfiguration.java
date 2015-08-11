package org.springframework.data.gremlin.object.tests.tinker.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gremlin.config.EnableGremlinRepositories;
import org.springframework.data.gremlin.object.jpa.TestService;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.gremlin.repository.tinker.TinkerGraphAdapter;
import org.springframework.data.gremlin.repository.tinker.TinkerGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.generator.SchemaGenerator;
import org.springframework.data.gremlin.schema.generator.jpa.JpaSchemaGenerator;
import org.springframework.data.gremlin.support.GremlinRepositoryFactoryBean;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.GremlinTransactionManager;
import org.springframework.data.gremlin.tx.tinker.TinkerGremlinGraphFactory;

@Configuration
@EnableGremlinRepositories(basePackages = "org.springframework.data.gremlin.object.jpa", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GremlinRepositoryWithNativeSupport.class) }, repositoryFactoryBeanClass = GremlinRepositoryFactoryBean.class)
public class Tinker_JPA_TestConfiguration {

    @Bean
    public TinkerGremlinGraphFactory factory() {
        TinkerGremlinGraphFactory factory = new TinkerGremlinGraphFactory();
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
        return new JpaSchemaGenerator();
    }

    @Bean
    public static GremlinBeanPostProcessor tinkerpopSchemaManager(SchemaGenerator schemaGenerator) {
        return new GremlinBeanPostProcessor(schemaGenerator, "org.springframework.data.gremlin.object.jpa.domain");
    }

    @Bean
    public GremlinGraphAdapter graphAdapter() {
        return new TinkerGraphAdapter();
    }

    @Bean
    public GremlinRepositoryContext databaseContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory) {
        return new GremlinRepositoryContext(graphFactory, graphAdapter, schemaFactory, null, TinkerGremlinRepository.class);
    }

    @Bean
    public TestService testService() {
        return new TestService();
    }
}
