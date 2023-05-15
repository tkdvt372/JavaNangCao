package com.dvt.coursesweb.service;

import com.cloudinary.Cloudinary;
import com.dvt.coursesweb.model.Course;
import com.dvt.coursesweb.repository.CourseRepository;
import com.dvt.coursesweb.repository.SearchRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchService implements SearchRepository {
    @Autowired
    MongoClient client;
    @Autowired
    MongoConverter converter;

    @Autowired
    CourseRepository repo;
    @Autowired
    Cloudinary cloudinary;
    public Optional<Course> singleCourse(ObjectId id){
        return repo.findById(String.valueOf(id));
    }

    @Override
    public ResponseEntity getAllCourse(String title, String category) {

        final List<Course> listCourses = new ArrayList<>();

        MongoDatabase database = client.getDatabase("courses-web");
        MongoCollection<Document> collection = database.getCollection("courses");
        if (title != null && category != null) {
            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                    new Document("text",
                            new Document("query", title)
                                    .append("path", "title"))
                            .append("text",
                                    new Document("query", category)
                                            .append("path", "category")))));
            result.forEach(doc -> listCourses.add(converter.read(Course.class, doc)));
            List<Course> output = listCourses.stream()
                    .map(course -> new Course(
                            course.getTitle()
                            , course.getDescription()
                            , course.getPoster()
                            , course.getViews()
                            , course.getNumOfVideos()
                            , course.getCategory()
                            , course.getCreatedBy()
                            , course.getCreatedAt()
                    )).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courses", output);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (category == null) {
            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                    new Document("text",
                            new Document("query", title)
                                    .append("path", "title")))));
            result.forEach(doc -> listCourses.add(converter.read(Course.class, doc)));
            List<Course> output = listCourses.stream()
                    .map(course -> new Course(
                            course.getTitle()
                            , course.getDescription()
                            , course.getPoster()
                            , course.getViews()
                            , course.getNumOfVideos()
                            , course.getCategory()
                            , course.getCreatedBy()
                            , course.getCreatedAt()
                    )).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courses", output);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else if(title == null){
            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                    new Document("text",
                            new Document("query", category)
                                    .append("path", "category")))));

            result.forEach(doc -> listCourses.add(converter.read(Course.class, doc)));
            List<Course> output = listCourses.stream()
                    .map(course -> new Course(
                            course.getTitle()
                            , course.getDescription()
                            , course.getPoster()
                            , course.getViews()
                            , course.getNumOfVideos()
                            , course.getCategory()
                            , course.getCreatedBy()
                            , course.getCreatedAt()
                    )).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courses", output);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return null;

    }

}
