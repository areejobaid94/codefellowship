package com.example.codefellowship.repositories;

import com.example.codefellowship.models.ApplicationUser;
import com.example.codefellowship.models.UsersFollowers;
import org.springframework.data.repository.CrudRepository;

public interface UsersFollowersRepository extends CrudRepository<UsersFollowers, Integer> {
}
