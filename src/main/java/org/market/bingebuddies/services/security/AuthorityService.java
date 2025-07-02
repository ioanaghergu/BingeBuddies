package org.market.bingebuddies.services.security;

import org.market.bingebuddies.domain.security.Authority;

public interface AuthorityService {
    Authority getAuthorityByRole(String roleName);
}
