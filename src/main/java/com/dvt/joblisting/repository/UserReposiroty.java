package com.dvt.joblisting.repository;

import com.dvt.joblisting.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReposiroty extends MongoRepository<User,String> {

}
