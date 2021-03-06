package org.egov.domain.service;

import org.egov.domain.TokenValidationFailureException;
import org.egov.domain.model.*;
import org.egov.persistence.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private TokenRepository tokenRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token create(TokenRequest tokenRequest) {
        tokenRequest.validate();
        return tokenRepository.save(tokenRequest);
    }

    public Token validate(ValidateRequest validateRequest) {
        validateRequest.validate();
        final Tokens tokens = tokenRepository
                .find(validateRequest);
        if( !tokens.hasSingleNonExpiredToken()) {
            logger.info("Token validation failure for otp #", validateRequest.getOtp());
            throw new TokenValidationFailureException();
        }
        final Token matchingToken = tokens.getNonExpiredToken();
        tokenRepository.markAsValidated(matchingToken);
        return matchingToken;
    }

    public Token search(TokenSearchCriteria searchCriteria) {
        return tokenRepository.findBy(searchCriteria);
    }
}
