package com.dvt.joblisting.model.submodel;

public class Avatar {
    private String public_id;
    private String url;

    public Avatar(String public_id, String url) {
        this.public_id = public_id;
        this.url = url;
    }

    public Avatar() {

    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
