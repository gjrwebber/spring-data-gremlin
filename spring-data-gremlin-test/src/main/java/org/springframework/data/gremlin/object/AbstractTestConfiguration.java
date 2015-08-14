package org.springframework.data.gremlin.object;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.repository.GremlinRepository;
import org.springframework.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.gremlin.schema.generator.BasicSchemaGenerator;
import org.springframework.data.gremlin.schema.generator.SchemaGenerator;
import org.springframework.data.gremlin.schema.writer.SchemaWriter;
import org.springframework.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.data.gremlin.tx.GremlinTransactionManager;

@Configuration
public abstract class AbstractTestConfiguration {

    @Bean
    public abstract GremlinGraphFactory factory();

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
        return new BasicSchemaGenerator();
    }

    @Bean
    public abstract SchemaWriter schemaWriter();

    @Bean
    public abstract GremlinGraphAdapter graphAdapter();

    protected Class<? extends GremlinRepository> getGremlinRepositoryType() {
        return GremlinRepository.class;
    }

    @Bean
    public GremlinRepositoryContext databaseContext(GremlinGraphFactory graphFactory, GremlinGraphAdapter graphAdapter, GremlinSchemaFactory schemaFactory, SchemaWriter schemaWriter) {
        return new GremlinRepositoryContext(graphFactory, graphAdapter, schemaFactory, schemaWriter, getGremlinRepositoryType());
    }

}
