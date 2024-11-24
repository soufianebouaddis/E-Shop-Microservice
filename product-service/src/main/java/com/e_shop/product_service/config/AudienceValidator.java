package com.e_shop.product_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final String audience;
    private static final Logger logger = LoggerFactory.getLogger(AudienceValidator.class);
    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        // Detailed logging
        logger.info("JWT Audiences: " + jwt.getAudience());
        logger.info("Expected Audience: " + audience);

        if (jwt.getAudience() != null && jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "Token does not have expected audience",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}