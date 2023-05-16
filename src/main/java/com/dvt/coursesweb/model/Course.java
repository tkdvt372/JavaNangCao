package com.dvt.coursesweb.model;

import com.dvt.coursesweb.model.submodel.Lecture;
import com.dvt.coursesweb.model.submodel.Poster;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private List<Lecture> lectures;

    private Poster poster;
    private int views;
    private int numOfVideos;
    private String category;
    private String createdBy;
    private Date createdAt;
    public Course(){
        this.createdAt = new Date();
        id = new ObjectId().toString();
        lectures = new ArrayList<>();
    }

    public Course(String id, String title, String description, Poster poster, int views, int numOfVideos, String category, String createdBy, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.views = views;
        this.numOfVideos = numOfVideos;
        this.category = category;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Course(String title, String description, List<Lecture> lectures, Poster poster, int views, int numOfVideos, String category, String createdBy, Date createdAt) {
        this.title = title;
        this.description = description;
        this.lectures = lectures;
        this.poster = poster;
        this.views = views;
        this.numOfVideos = numOfVideos;
        this.category = category;
        this.createdBy = createdBy;
        this.createdAt = createdAt;

    }


    public Course(String title, String description, Poster poster, int views, int numOfVideos, String category, String createdBy, Date createdAt) {
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.views = views;
        this.numOfVideos = numOfVideos;
        this.category = category;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getNumOfVideos() {
        return numOfVideos;
    }

    public void setNumOfVideos(int numOfVideos) {
        this.numOfVideos = numOfVideos;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }
}
