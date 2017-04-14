package org.springframework.data.gremlin.object.tests.janus.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gremlin.config.EnableGremlinRepositories;
import org.springframework.data.gremlin.object.core.TestService;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.gremlin.repository.janus.JanusGraphAdapter;
import org.springframework.data.gremlin.repository.janus.JanusGremlinRepository;
import org.springframework.data.gremlin.schema.GremlinBeanPostProcessor;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.schema.writer.janus.JanusSchemaWriter;
import org.springframework.data.gremlin.support.GremlinRepositoryFactoryBean;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.GremlinTransactionManager;
import org.springframework.data.gremlin.tx.janus.JanusGremlinGraphFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Created by mmichail (zifnab87) on 13/04/17 based on gman's titan files.
 */

@Configuration
@EnableTransactionManagement
@EnableGremlinRepositories(basePackages = "org.springframework.data.gremlin.object.core", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GremlinRepositoryWithNativeSupport.class) }, repositoryFactoryBeanClass = GremlinRepositoryFactoryBean.class)
public class Janus_Core_TestConfiguration {

    @Bean
    public JanusGremlinGraphFactory factory() {
        JanusGremlinGraphFactory factory = new JanusGremlinGraphFactory();
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
    public SchemaWriter schemaWriter() {
        return new JanusSchemaWriter();
    }

    @Bean
    public GremlinGraphAdapter graphAdapter() {
        return new JanusGraphAdapter();
    }

    @Bean
    public static GremlinBeanPostProcessor tinkerpopSchemaManager() {
        return new GremlinBeanPostProcessor("org.springframework.data.gremlin.object.core.domain");
    }

    @Bean
    public GremlinRepositoryContext databaseContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter) {
        return new GremlinRepositoryContext(graphFactory, graphAdapter, schemaFactory, schemaWriter, JanusGremlinRepository.class);
    }

    @Bean
    public TestService testService() {
        return new TestService();
    }
}
