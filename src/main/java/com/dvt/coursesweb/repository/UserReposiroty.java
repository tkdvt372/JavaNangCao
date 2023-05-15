package com.dvt.coursesweb.repository;

import com.dvt.coursesweb.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReposiroty extends MongoRepository<User,String> {

}
