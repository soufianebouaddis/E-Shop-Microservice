package com.e_shop.config_server.repository;

import com.e_shop.config_server.model.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertySourceRepository extends JpaRepository<PropertySource,Integer> {
    long countByApplicationName(String applicationName);
}
