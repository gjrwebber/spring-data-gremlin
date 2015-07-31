package org.springframework.data.gremlin.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * {@link org.springframework.context.annotation.ImportBeanDefinitionRegistrar} to enable {@link EnableGremlinRepositories} annotation.
 *
 * @author Gman
 */
public class GremlinRepositoryRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableGremlinRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new GremlinRepositoryConfigExtension();
    }
}
