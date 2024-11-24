package com.e_shop.config_server.config;

import com.e_shop.config_server.model.PropertySource;
import com.e_shop.config_server.repository.PropertySourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Initializer {
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final PropertySourceRepository configManager;


    public Initializer(PropertySourceRepository configManager) {
        this.configManager = configManager;
    }

    @Bean
    public CommandLineRunner initializeConfigData() {
        return args -> {
            List<String> applications = Arrays.asList("AUTH-SERVICE", "API-GATEWAY", "DISCOVERY-SERVICE", "PRODUCT-SERVICE");

            applications.forEach(applicationName -> {
                if (configManager.countByApplicationName(applicationName) == 0) {
                    insertProperties(applicationName);
                    logger.info("Inserted properties for {}", applicationName);
                } else {
                    logger.info("Properties for {} already exist. Skipping.", applicationName);
                }
            });
        };
    }

    private void insertProperties(String applicationName) {
        switch (applicationName) {
            case "AUTH-SERVICE" -> {
                saveProperty(applicationName, "default", "master", "server.port", "8280");
                saveProperty(applicationName, "default", "master", "spring.datasource.url", "jdbc:mysql://localhost:3306/authDB?createDatabaseIfNotExist=true");
                saveProperty(applicationName, "default", "master", "spring.datasource.username", "root");
                saveProperty(applicationName, "default", "master", "spring.datasource.password", "root");
            }
            case "API-GATEWAY" -> {
                saveProperty(applicationName, "default", "master", "server.port", "8180");
                saveProperty(applicationName, "default", "master", "spring.cloud.config.uri", "http://localhost:8380");
            }
            case "DISCOVERY-SERVICE" -> {
                saveProperty(applicationName, "default", "master", "server.port", "8761");
            }
            case "PRODUCT-SERVICE" -> {
                saveProperty(applicationName, "default", "master", "server.port", "8380");
                saveProperty(applicationName, "default", "master", "spring.datasource.url", "jdbc:mysql://localhost:3306/product?createDatabaseIfNotExist=true");
                saveProperty(applicationName, "default", "master", "spring.datasource.username", "root");
                saveProperty(applicationName, "default", "master", "spring.datasource.password", "root");
            }
            default -> logger.warn("No properties found for application: {}", applicationName);
        }
    }

    private void saveProperty(String applicationName, String profile, String label, String key, String value) {
        PropertySource property = new PropertySource();
        property.setApplicationName(applicationName);
        property.setProfile(profile);
        property.setLabel(label);
        property.setPropertyKey(key);
        property.setPropertyValue(value);
        this.configManager.save(property);
    }
}
