package com.tca.UserManager.provider.factory;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Custom PropertySourceFactory for loading YAML files as property sources.
 * This allows Spring's @PropertySource annotation to read YAML configuration files.
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    /**
     * Loads the YAML file and returns it as a PropertySource.
     * @param name The name of the property source (can be null)
     * @param resource The encoded resource representing the YAML file
     * @return A PropertySource containing the loaded properties
     * @throws IOException if the resource cannot be read
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws
            IOException {
        // Load the YAML file into a Properties object
        Properties loadedProperties = this.loadYamlIntoProperties(resource.getResource());
        // Return a new PropertySource with the loaded properties
        return new PropertiesPropertySource(
                (StringUtils.hasLength(name)) ? name : resource.getResource().getFilename(),
                loadedProperties
        );
    }

    /**
     * Helper method to load YAML content into a Properties object.
     * @param resource The resource representing the YAML file
     * @return Properties loaded from the YAML file
     */
    private Properties loadYamlIntoProperties(Resource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
