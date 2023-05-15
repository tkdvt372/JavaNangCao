package com.dvt.joblisting.repository;

import com.dvt.joblisting.model.Course;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository {
    public List<Course> getAllCourse(String title,String category);
    public Optional<Course> singleCourse(ObjectId id);

}
