package com.e_shop.auth_service.mapper.service;

public interface MapperService <Source, Destination> {
    Source mapToEntity(Destination destination);

    Destination mapFromEntity(Source source);
}

