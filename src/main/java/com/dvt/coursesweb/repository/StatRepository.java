package com.dvt.coursesweb.repository;

import com.dvt.coursesweb.model.Stat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatRepository extends MongoRepository<Stat,String> {
}
