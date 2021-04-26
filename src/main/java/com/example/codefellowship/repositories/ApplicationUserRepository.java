package com.example.codefellowship.repositories;

import com.example.codefellowship.models.ApplicationUser;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Integer> {
    public ApplicationUser findByUsername(String userName);
}
