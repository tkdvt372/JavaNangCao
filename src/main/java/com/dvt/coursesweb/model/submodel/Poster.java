package com.dvt.coursesweb.model.submodel;

public class Poster {
    private String public_id;
    private String url;

    public Poster(String public_id, String url) {
        this.public_id = public_id;
        this.url = url;
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
