package com.dvt.joblisting.model;

import com.dvt.joblisting.model.submodel.Avatar;
import com.dvt.joblisting.model.submodel.Playlist;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User{
    @Id
    private ObjectId id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String subscription;
    private Avatar avatar;

    private List<Playlist> playlist;
    private Date createdAt;
    private String resetPasswordToken;
    private String resetPasswordExpire;



    public User(String name, String email, String password, String role, String subscription, Avatar avatar, List<Playlist> playlist, Date createdAt, String resetPasswordToken, String resetPasswordExpire) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.subscription = subscription;
        this.avatar = avatar;
        this.playlist = playlist;
        this.createdAt = createdAt;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordExpire = resetPasswordExpire;
    }
    public ObjectId getId() {
        return id;
    }
    public User() {
        this.createdAt = new Date();
        this.role = "user";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public List<Playlist> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<Playlist> playlist) {
        this.playlist = playlist;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public String getResetPasswordExpire() {
        return resetPasswordExpire;
    }

    public void setResetPasswordExpire(String resetPasswordExpire) {
        this.resetPasswordExpire = resetPasswordExpire;
    }


}
