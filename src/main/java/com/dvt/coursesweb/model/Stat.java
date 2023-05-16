package com.dvt.coursesweb.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "stats")
public class Stat {
    @Id
    private String id;
    private int users;
    private int subscription;
    private int views;
    private Date createdAt;
    public Stat(){
        this.id = new ObjectId().toString();
        this.createdAt = new Date();
    }

    public Stat(int users, int subscription, int views, Date createdAt) {
        this.users = users;
        this.subscription = subscription;
        this.views = views;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getSubscription() {
        return subscription;
    }

    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
