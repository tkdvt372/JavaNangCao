package com.dvt.coursesweb.model;

import com.dvt.coursesweb.model.submodel.Avatar;
import com.dvt.coursesweb.model.submodel.Playlist;
import com.dvt.coursesweb.model.submodel.Subscription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User{
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Subscription subscription;
    private Avatar avatar;

    private List<Playlist> playlist;
    private Date createdAt;
    private String resetPasswordToken;
    private String resetPasswordExpire;



    public User(String name, String email, String password, String role, Subscription subscription, Avatar avatar, List<Playlist> playlist, Date createdAt, String resetPasswordToken, String resetPasswordExpire) {
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
    public String getId() {
        return id;
    }
    public User() {
        this.createdAt = new Date();
        this.role = "user";
        this.id = new ObjectId().toString();
        this.resetPasswordToken = "";
        this.resetPasswordExpire = "";
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
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

    public String getResetToken() {
        String resetToken = generateRandomToken();
        this.resetPasswordToken = generateHash(resetToken);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println((LocalDateTime.now().plusMinutes(15)).format(formatter));
        this.resetPasswordExpire = (LocalDateTime.now().plusMinutes(15)).format(formatter);
        return resetToken;
    }

    private String generateRandomToken() {

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[20];
        secureRandom.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }

    private String generateHash(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(input.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
