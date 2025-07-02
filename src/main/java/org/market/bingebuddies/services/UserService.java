package org.market.bingebuddies.services;

import org.market.bingebuddies.domain.security.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    User save(User user);
}
