package org.market.bingebuddies.controllers;

import jakarta.validation.Valid;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.UserDTO;
import org.market.bingebuddies.mappers.UserMapper;
import org.market.bingebuddies.services.UserService;
import org.market.bingebuddies.services.security.AuthorityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;
    private AuthorityService authorityService;

    public AuthController(UserMapper userMapper, UserService userService, AuthorityService authorityService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated())
            return "redirect:/";

        return "login";
    }

    @GetMapping("/register")
    public String displayRegisterForm(Authentication authentication, Model model) {
        if(authentication != null && authentication.isAuthenticated())
            return "redirect:/";

        UserDTO user = UserDTO.builder().build();
        model.addAttribute("user", user);
        return "register";

    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserDTO user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        try {
            User newUser = userMapper.toUser(user);
            newUser.setAuthorities(Set.of(authorityService.getAuthorityByRole("ROLE_GUEST")));
            userService.save(newUser);
            return "redirect:/login?created";
        }
        catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Registration failed. " + e.getMessage() + " Please try again.");
            return "register";
        }

    }
}
