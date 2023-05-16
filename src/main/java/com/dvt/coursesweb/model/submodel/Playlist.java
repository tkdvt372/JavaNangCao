package com.dvt.coursesweb.model.submodel;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Playlist {
    @Id
    private String id;
    private String course;

    private String poster;
    public Playlist(){
        id= new ObjectId().toString();
    }

    public Playlist(String id, String course, String poster) {
        this.id = id;
        this.course = course;
        this.poster = poster;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
