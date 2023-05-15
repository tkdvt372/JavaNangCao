package com.dvt.joblisting.model.submodel;

import java.util.Date;

public class Subscription {
    private String id;
    private String status;
    private Date createdTime;

    public Subscription(){
        
    }
    public Subscription(String id, String status, Date createdTime) {
        this.id = id;
        this.status = status;
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
