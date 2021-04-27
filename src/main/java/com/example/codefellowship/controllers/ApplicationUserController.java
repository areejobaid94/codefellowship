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
                               @RequestParam("dateOfBirth") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @RequestParam(value="bio") String bio,
                               @RequestParam(value="isAdmin") boolean isAdmin){

        String defaultImgUrl = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
        ApplicationUser newUser = new ApplicationUser(bCryptPasswordEncoder.encode(password), username,  firstName,  lastName,  dateOfBirth,  defaultImgUrl,  bio,isAdmin);
        try{
            newUser = applicationUserRepository.save(newUser);
        }catch (Exception ex){
            return new RedirectView("/error?message=Used%username");
        }
        return new RedirectView("/login");
    }

    @RequestMapping(value = "/delete_user", method = RequestMethod.DELETE)
    public RedirectView handleDeleteUser(@RequestParam(value = "id") Integer id,Principal p) {
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();

        if(userDetails.isAdmin || userDetails.getId() == id){
            applicationUserRepository.deleteById(id);
            return new RedirectView("/perform_logout");
        }
        return new RedirectView("/error?message=You%are%not%allow%to%delete%the%user");
    }
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public RedirectView handleGetError(@RequestParam(value = "message") String message, Model m) {
        m.addAttribute("message",message);
        return new RedirectView("/error");
    }

    @RequestMapping(value = "/user_Data", method = RequestMethod.GET)
    public String handleUserData() {
        return "SearchUser";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String handleUserData(@RequestParam(value = "id") Integer id,Principal p, Model m) {
        if(((UsernamePasswordAuthenticationToken) p) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        System.out.println("helllo");
        ApplicationUser applicationUser =(ApplicationUser) applicationUserRepository.findById(id).get();
        if(applicationUser != null){
            if(((UsernamePasswordAuthenticationToken) p) != null){
                ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
                if((id == userDetails.getId()) || userDetails.isAdmin){
                    m.addAttribute("isAllow", true);
                }else {
                    m.addAttribute("isAllow", false);
                }
            }
            m.addAttribute("user", applicationUser);
            return "profile";
        }
        return "/error?message=Id%is%not%used";
    }

    @GetMapping("/myprofile")
    public String getUserProfilePage(Principal p,Model m){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        if(userDetails != null){
            m.addAttribute("user", applicationUserRepository.findById (userDetails.getId()).get());
            m.addAttribute("username", applicationUserRepository.findById (userDetails.getId()).get().getUsername());
            return "profile";
        }
        m.addAttribute("message", "You%are%not%allow%to%delete%the%user");
        return "/error";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable(value = "id") Integer id,Principal p, Model m){
        if(((UsernamePasswordAuthenticationToken) p) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        System.out.println("helllo");
        ApplicationUser applicationUser =(ApplicationUser) applicationUserRepository.findById(id).get();
        if(applicationUser != null){
            if(((UsernamePasswordAuthenticationToken) p) != null){
                ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
                if((id == userDetails.getId()) || userDetails.isAdmin){
                    m.addAttribute("isAllow", true);
                }else {
                    m.addAttribute("isAllow", false);
                }
            }
            m.addAttribute("user", applicationUser);
            return "profile";
        }
        m.addAttribute("message", "Id%is%not%used");
        return "/error";
    }

    @PutMapping("/user-update")
    public RedirectView updateStudent(@RequestParam(value="username") String username, @RequestParam(value="imgUrl") String imgUrl,
    @RequestParam(value="firstName") String firstName, @RequestParam(value="lastName") String lastName,
    @RequestParam("dateOfBirth") @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth, @RequestParam(value="bio") String bio,Principal p, @RequestParam(value="id") Integer id){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        if(userDetails.isAdmin || userDetails.getId() == id){
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
        return new RedirectView("/error?message=You%are%not%allow%to%delete%the%user");
    }
}