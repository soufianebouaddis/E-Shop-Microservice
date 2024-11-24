package com.e_shop.config_server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PropertySource", uniqueConstraints = @UniqueConstraint(columnNames = {"application_name", "profile", "label", "property_key"}))
public class PropertySource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Column(name = "profile", nullable = false)
    private String profile;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "property_key", nullable = false)
    private String propertyKey;

    @Column(name = "property_value", nullable = false)
    private String propertyValue;

    public Long getId() {
        return id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getProfile() {
        return profile;
    }

    public String getLabel() {
        return label;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
