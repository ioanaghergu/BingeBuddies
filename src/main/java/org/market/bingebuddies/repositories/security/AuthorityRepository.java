package org.market.bingebuddies.repositories.security;

import org.market.bingebuddies.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findAuthorityByRole(String role);
}
