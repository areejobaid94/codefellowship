package com.example.codefellowship.controllers;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.models.Post;
import com.example.codefellowship.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;

    @GetMapping("/posts")
    public String posts(Model m, Principal p) {
        if(((UsernamePasswordAuthenticationToken) p) != null){
            UserDetails userDetails =(ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
            m.addAttribute("username", userDetails.getUsername());
        }
        m.addAttribute("posts" ,postRepository.findAll());
        System.out.println(postRepository.findAll());
        return "posts";
    }


    @RequestMapping(value = "/delete_post", method = RequestMethod.GET)
    public RedirectView handleDeletePost(@RequestParam(value = "id") Integer id) {
        postRepository.deleteById(id);
        return new RedirectView("/posts");
    }

    @PostMapping("/post")
    public RedirectView addStudent(@RequestParam(value = "body") String body, Principal p, String imageUrl){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        System.out.println(userDetails);
        System.out.println("userDetails");
        Post post = new Post(body,userDetails, imageUrl);
        postRepository.save(post);
        System.out.println(post);
        return  new RedirectView("/myprofile");
    }

    @PostMapping("/post-update")
    public RedirectView updateStudent(@RequestParam(value = "body") String body ,
                                      @RequestParam(value="id") Integer id,String imageUrl,Principal p){
        System.out.println(body);
        System.out.println("id");
        Post post = postRepository.findById(id).get();
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        post.setBody(body);
        post.setImageUrl(imageUrl);
        postRepository.save(post);
        return  new RedirectView("/myprofile");
    }
}
