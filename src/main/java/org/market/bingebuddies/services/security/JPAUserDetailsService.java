package org.market.bingebuddies.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.market.bingebuddies.domain.security.Authority;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
//@Profile("mysql")
public class JPAUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user;
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            user = userOpt.get();
        }
        else {
            throw new UsernameNotFoundException("Username not found: " + username);
        }

        log.info(user.toString());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getEnabled(), user.getAccountNonExpired(),
                user.getCredentialsNonExpired(), user.getAccountNonLocked(),
                getAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Authority> authorities) {
        if(authorities == null) {
            return new HashSet<>();
        } else if (authorities.size() == 0) {
            return new HashSet<>();
        }
        else {
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }
}
