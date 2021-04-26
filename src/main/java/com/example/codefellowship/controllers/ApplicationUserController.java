package com.example.codefellowship.controllers;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/signup")
    public String getSignUpPage(Principal p, Model m){
        if(((UsernamePasswordAuthenticationToken) p) != null){
            UserDetails userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        return "signup";
    }

    @GetMapping("/login")
    public String getLoginPage(Principal p, Model m){
        if(((UsernamePasswordAuthenticationToken) p) != null){
            UserDetails userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        return "login";
    }

    @PostMapping("/signup")
    public RedirectView signup(@RequestParam(value="username") String username, @RequestParam(value="password") String password,
                               @RequestParam(value="firstName") String firstName, @RequestParam(value="lastName") String lastName,
                               @RequestParam("dateOfBirth") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @RequestParam(value="bio") String bio){

        String defaultImgUrl = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
        ApplicationUser newUser = new ApplicationUser(bCryptPasswordEncoder.encode(password), username,  firstName,  lastName,  dateOfBirth,  defaultImgUrl,  bio);
        newUser = applicationUserRepository.save(newUser);
        return new RedirectView("/login");
    }

    @RequestMapping(value = "/delete_user", method = RequestMethod.GET)
    public RedirectView handleDeleteUser(@RequestParam(value = "id") Integer id) {
        applicationUserRepository.deleteById(id);
        return new RedirectView("/perform_logout");
    }

    @RequestMapping(value = "/user_Data", method = RequestMethod.GET)
    public String handleUserData() {
        return "SearchUser";
    }
    @GetMapping("/myprofile")
    public String getUserProfilePage(Principal p,Model m){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        m.addAttribute("user", applicationUserRepository.findById (userDetails.getId()).get());
        return "profile";
    }
    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable(value = "id") Integer id,Principal p, Model m){
        if(((UsernamePasswordAuthenticationToken) p) != null){
            UserDetails userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        m.addAttribute("user", (applicationUserRepository.findById(id).get()));
        return "profile";
    }

    @PostMapping("/user-update")
    public RedirectView updateStudent(@RequestParam(value="username") String username, @RequestParam(value="imgUrl") String imgUrl,
    @RequestParam(value="firstName") String firstName, @RequestParam(value="lastName") String lastName,
    @RequestParam("dateOfBirth") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @RequestParam(value="bio") String bio,Principal p){

        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        ApplicationUser userToSave = applicationUserRepository.findById(userDetails.getId()).get();
        userToSave.setBio(bio);
        userToSave.setDateOfBirth(dateOfBirth);
        userToSave.setUsername(username);
        userToSave.setFirstName(firstName);
        userToSave.setImgUrl(imgUrl);
        userToSave.setLastName(lastName);
        userToSave.setImgUrl(imgUrl);
        applicationUserRepository.save(userToSave);
        return  new RedirectView("/myprofile");
    }
}