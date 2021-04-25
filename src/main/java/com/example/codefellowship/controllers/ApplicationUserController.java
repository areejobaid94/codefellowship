package com.example.codefellowship.controllers;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Date;


@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/signup")
    public String getSignUpPage(){
        return "signup.html";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login.html";
    }
    @PostMapping("/signup")
    public RedirectView signup(@RequestParam(value="username") String username, @RequestParam(value="password") String password,
                               @RequestParam(value="firstName") String firstName, @RequestParam(value="lastName") String lastName,
                               @RequestParam("dateOfBirth") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @RequestParam(value="bio") String bio){
        String defaultImgUrl = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
        ApplicationUser newUser = new ApplicationUser(password, firstName, lastName, dateOfBirth, defaultImgUrl,  bio, username);
        newUser = applicationUserRepository.save(newUser);
        return new RedirectView("/login");
    }

    @GetMapping("/myprofile")
    public String getUserProfilePage(Principal p, Model m){
        m.addAttribute("user", ((UsernamePasswordAuthenticationToken)p).getPrincipal());
        return "profile.html";
    }

}