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


    @RequestMapping(value = "/delete_post", method = RequestMethod.DELETE)
    public RedirectView handleDeletePost(@RequestParam(value = "id") Integer id,Principal p) {
        System.out.println(id);
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        Post post = postRepository.findById(id).get();
        if(userDetails.getId() == post.getApplicationUser().getId() || post.getApplicationUser().isAdmin){
            postRepository.deleteById(id);
            return new RedirectView("/posts");
        }
        return new RedirectView("/error?message=You%are%not%allow%to%delete%the%user");
    }

    @PostMapping("/post")
    public RedirectView addStudent(@RequestParam(value = "body") String body, Principal p, String imageUrl,@RequestParam(value="isPublic",defaultValue = "false") boolean isPublic){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        System.out.println(userDetails);
        System.out.println("userDetails");
        Post post = new Post(body,userDetails, imageUrl,isPublic);
        postRepository.save(post);
        System.out.println(post);
        return  new RedirectView("/myprofile");
    }

    @PutMapping("/post-update")
    public RedirectView updateStudent(@RequestParam(value = "body") String body ,
                                      @RequestParam(value="id") Integer id,String imageUrl,Principal p,@RequestParam(value="isPublic",defaultValue = "false") boolean isPublic){
        System.out.println(body);
        System.out.println("id");
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        Post post = postRepository.findById(id).get();
        if(userDetails.getId() == post.getApplicationUser().getId()  || post.getApplicationUser().isAdmin){
            post.setBody(body);
            post.setPublic(isPublic);
            post.setImageUrl(imageUrl);
            postRepository.save(post);
            return  new RedirectView("/myprofile");
        }
        return new RedirectView("/error?message=You%are%not%allow%to%delete%the%user");
    }
}
