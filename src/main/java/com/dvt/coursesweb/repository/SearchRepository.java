package com.dvt.coursesweb.repository;

import com.dvt.coursesweb.model.Course;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository {
    public ResponseEntity getAllCourse(String title, String category);
    public Optional<Course> singleCourse(ObjectId id);

}
