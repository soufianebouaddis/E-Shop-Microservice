package com.e_shop.product_service.mapper.service;

public interface Mapper<Source,Destination>{
    Destination fromEntity(Source source);
    Source toEntity(Destination destination);
}
