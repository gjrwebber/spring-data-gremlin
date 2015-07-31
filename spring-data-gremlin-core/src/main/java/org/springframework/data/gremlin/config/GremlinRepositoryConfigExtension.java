package org.springframework.data.gremlin.config;

import org.springframework.data.gremlin.support.GremlinRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

/**
 * {@link org.springframework.data.repository.config.RepositoryConfigurationExtension} for Gremlin.
 *
 * @author Gman
 */
public class GremlinRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryClassName()
     */
    public String getRepositoryFactoryClassName() {
        return GremlinRepositoryFactoryBean.class.getName();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getModulePrefix()
     */
    @Override
    protected String getModulePrefix() {
        return "gremlin";
    }


}
