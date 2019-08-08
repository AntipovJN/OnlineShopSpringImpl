package com.boraji.tutorial.spring.controller;

import com.boraji.tutorial.spring.entity.User;
import com.boraji.tutorial.spring.service.UserService;
import com.boraji.tutorial.spring.utils.SHA256StringHashUtil;
import com.boraji.tutorial.spring.utils.SaltGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.security.auth.login.LoginException;
import java.util.Optional;

@Controller
@SessionAttributes("user")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/users")
    public String usersPageView(Model model, @ModelAttribute("user") User user) {
        if (user.getRole().equals("admin")) {
            model.addAttribute("users", userService.getAll());
            return "users";
        }
        return "redirect:/products/store";
    }

    @RequestMapping(value = "/users/remove", method = RequestMethod.GET)
    public String removeUser(@RequestParam String id, @ModelAttribute("user") User user) {
        if (user.getRole().equals("admin")) {
            userService.removeUser(Long.valueOf(id));
        }
        return "redirect:/users";
    }

    @RequestMapping(value = "/users/edit", method = RequestMethod.GET)
    public String editUserPageView(Model model, @RequestParam String id,
                                   @ModelAttribute("user") User user) {
        if (user.getRole().equals("admin")) {
            Optional<User> optionalUser = userService.getById(Long.valueOf(id));
            if (optionalUser.isPresent()) {
                model.addAttribute("id", id);
                model.addAttribute("salt", optionalUser.get().getSalt());
                model.addAttribute("email", optionalUser.get().getEmail());
                return "edit_user";
            }
        }
        return "redirect:/users";
    }

    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public String editUser(@ModelAttribute("user") User user,
                           @RequestParam String id,
                           @RequestParam String salt,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatPassword,
                           Model model) {
        if (user.getRole().equals("admin")) {
            try {
                userService.updateUser(Long.valueOf(id), email,
                        SHA256StringHashUtil.getSha256(SaltGeneratorUtil.saltPassword(password, salt)),
                        SHA256StringHashUtil.getSha256(SaltGeneratorUtil.saltPassword(repeatPassword, salt)));
            } catch (LoginException e) {
                model.addAttribute("error", e.getMessage());
                return "edit_user";
            }
        }
        return "redirect:/users";
    }
}
