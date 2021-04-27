package com.example.codefellowship.controllers;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.models.Post;
import com.example.codefellowship.models.UsersFollowers;
import com.example.codefellowship.repositories.ApplicationUserRepository;
import com.example.codefellowship.repositories.UsersFollowersRepository;
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
import java.util.Set;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UsersFollowersRepository usersFollowersRepository;

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
                               @RequestParam(value="isAdmin", defaultValue = "false") boolean isAdmin){

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
            if(userDetails.isAdmin || id == userDetails.getId()){
                m.addAttribute("isAllow", true);
            }else {
                m.addAttribute("isAllow", false);
            }
        }
        ApplicationUser applicationUser =(ApplicationUser) applicationUserRepository.findById(id).get();
        if(applicationUser != null){
            m.addAttribute("user", applicationUser);
            return "profile";
        }
        return "/error?message=You%are%not%allow%to%delete%the%user";
    }

    @GetMapping("/myprofile")
    public String getUserProfilePage(Principal p,Model m){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        m.addAttribute("user", applicationUserRepository.findById (userDetails.getId()).get());
        m.addAttribute("username", applicationUserRepository.findById (userDetails.getId()).get().getUsername());
        m.addAttribute("isAllow", true);

        return "profile";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable(value = "id") Integer id,Principal p, Model m){
        if(((UsernamePasswordAuthenticationToken) p) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
            if(userDetails.isAdmin || id == userDetails.getId()){
                m.addAttribute("isAllow", true);
            }else {
                m.addAttribute("isAllow", false);
            }
        }
        System.out.println("helllo");
        ApplicationUser applicationUser =(ApplicationUser) applicationUserRepository.findById(id).get();
        if(applicationUser != null){
            m.addAttribute("user", applicationUser);
            return "profile";
        }
        return "/error?message=You%are%not%allow%to%delete%the%user";
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

    @RequestMapping(value = "/allUsers", method = RequestMethod.GET)
    public String handleGetAllUsersData(Model m, Principal p) {
        if(((UsernamePasswordAuthenticationToken) p
        ) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }

        m.addAttribute("users", applicationUserRepository.findAll());
        return "allUsers";
    }
    @RequestMapping(value = "/follow/{id}", method = RequestMethod.GET)
    public RedirectView handleFollowUser(Model m, @PathVariable(value = "id") Integer id,Principal p) {

        if(((UsernamePasswordAuthenticationToken) p) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            ApplicationUser applicationUser = applicationUserRepository.findById(userDetails.getId()).get();
            ApplicationUser followedUser = applicationUserRepository.findById(id).get();
            System.out.println(applicationUser);
            System.out.println(followedUser);
            UsersFollowers usersFollowers = new UsersFollowers(applicationUser,followedUser);
            usersFollowersRepository.save(usersFollowers);
            System.out.println(usersFollowersRepository.findAll());

        }
        return new RedirectView("/myprofile");


    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    public String handleFeed(Model m,Principal p) {
        if(((UsernamePasswordAuthenticationToken) p) != null){
            ApplicationUser userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            ApplicationUser applicationUser = applicationUserRepository.findById(userDetails.getId()).get();
            ArrayList<Post> allFollowerPosts = new ArrayList();
            Set<UsersFollowers> allFollower  =  applicationUser.getUsers();
            for (UsersFollowers user:  allFollower){
                allFollowerPosts.addAll(user.getApplicationUserFollower().getPosts());
            }
            m.addAttribute("username",userDetails.getUsername());

            m.addAttribute("posts",allFollowerPosts);
        }
        return "posts";
    }
}