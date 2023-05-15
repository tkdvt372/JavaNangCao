package com.dvt.coursesweb.model.submodel;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Playlist {
    @Id
    private ObjectId id;
    private ObjectId course;

    private String poster;
    public Playlist(){
        this.id= new ObjectId();
        this.course = new ObjectId();
    }

    public Playlist(ObjectId id, ObjectId course, String poster) {
        this.id = id;
        this.course = course;
        this.poster = poster;
    }

    public ObjectId getCourse() {
        return course;
    }

    public void setCourse(ObjectId course) {
        this.course = course;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
