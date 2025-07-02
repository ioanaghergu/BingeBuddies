package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.security.Authority;
import org.market.bingebuddies.repositories.security.AuthorityRepository;
import org.market.bingebuddies.services.security.AuthorityService;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Authority getAuthorityByRole(String roleName) {
        return authorityRepository.findAuthorityByRole(roleName);
    }
}
