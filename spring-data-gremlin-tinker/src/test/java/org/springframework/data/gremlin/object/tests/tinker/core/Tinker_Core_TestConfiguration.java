package org.springframework.data.gremlin.object.tests.tinker.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gremlin.config.EnableGremlinRepositories;
import org.springframework.data.gremlin.object.core.TestService;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.gremlin.repository.tinker.TinkerGraphAdapter;
import org.springframework.data.gremlin.repository.tinker.TinkerGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.support.GremlinRepositoryFactoryBean;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.GremlinTransactionManager;
import org.springframework.data.gremlin.tx.tinker.TinkerGremlinGraphFactory;

@Configuration
@EnableGremlinRepositories(basePackages = "org.springframework.data.gremlin.object.core", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GremlinRepositoryWithNativeSupport.class) }, repositoryFactoryBeanClass = GremlinRepositoryFactoryBean.class)
public class Tinker_Core_TestConfiguration {

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
    public static GremlinBeanPostProcessor tinkerpopSchemaManager() {
        return new GremlinBeanPostProcessor("org.springframework.data.gremlin.object.core.domain");
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
