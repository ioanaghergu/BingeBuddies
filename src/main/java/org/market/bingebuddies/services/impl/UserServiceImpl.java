package org.market.bingebuddies.services.impl;

import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.exceptions.UserAlreadyExists;
import org.market.bingebuddies.exceptions.UserNotFoundException;
import org.market.bingebuddies.repositories.security.UserRepository;
import org.market.bingebuddies.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        return user.get();
    }


    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new UserNotFoundException("User " + username + " not found");
        }
        return user.get();
    }

    @Override
    public User save(User user) {
        Optional<User> dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser.isPresent()) {
            throw new UserAlreadyExists("User " + user.getUsername() + " already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
