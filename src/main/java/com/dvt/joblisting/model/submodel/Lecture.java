package com.dvt.joblisting.model.submodel;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class Lecture {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private Video video;
    public Lecture(){
        id = new ObjectId();
    }

    public Lecture(String title, String description, Video video) {
        this.title = title;
        this.description = description;
        this.video = video;
    }

    public ObjectId getId() {
        return id;
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

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
