package com.dvt.coursesweb.controller;

import com.dvt.coursesweb.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserSevice userSevice;


    //Register
    @PostMapping("/register")
    public ResponseEntity Register(HttpServletResponse res, @RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("password") String password) throws Exception {
        return userSevice.Register(res,file,name,email,password);
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity Login(HttpServletResponse res,@RequestBody Map<String, Object> requestBody) throws Exception {
        String email = (String) requestBody.get("email");
        String password = (String) requestBody.get("password");
        return userSevice.Login(res,email,password);
    }

    //Logout
    @GetMapping("/logout")
    public ResponseEntity Logout(HttpServletResponse res){
        return userSevice.Logout(res);
    }


    //Get my profile
    @GetMapping("/me")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity getMyProfile(HttpServletRequest request) {
            return userSevice.GetMyProfile(request);
    }



    //Change password
    @PutMapping("/change-password")
    public ResponseEntity changePassword(HttpServletRequest request,@RequestBody Map<String, Object> requestBody){
        String oldPassword = (String) requestBody.get("oldPassword");
        String newPassword = (String) requestBody.get("newPassword");
        if(oldPassword == null){
            oldPassword = "";
        }
        if(newPassword == null){
            newPassword = "";
        }
        return userSevice.ChangePassword(request,oldPassword,newPassword);
    }

    //Update profile
    @PutMapping("/update-profile")
    public ResponseEntity updateProfile(HttpServletRequest request,@RequestBody Map<String, Object> requestBody){
        String name = (String) requestBody.get("name");
        String email = (String) requestBody.get("email");
        if(name == null){
            name = " ";
        }
        System.out.println(email);
        if(email == null){
            email = " ";
        }
        return userSevice.updateProfile(request,name,email);
    }

    //Update profile picture
    @PutMapping("/update-profile-picture")
    public ResponseEntity updateProfilePicture(HttpServletRequest request,@RequestParam("file") MultipartFile file){
        return userSevice.updateProfilePicture(request,file);
    }

    //Add to playlist
    @PostMapping("/add-to-playlist")
    public ResponseEntity addToPlaylist(HttpServletRequest request,@RequestBody Map<String, Object> requestBody){
        String id = (String) requestBody.get("id");
        return userSevice.addToPlaylist(request,id);
    }

    //Remove a course from playlist
    @DeleteMapping("/remove-from-playlist")
    public ResponseEntity removeFromPlaylist(HttpServletRequest request,@RequestParam("id") String id){
        return userSevice.RemoveFromPlaylist(request,id);
    }

    @PostMapping("/forget-password")
    public ResponseEntity forgetPassword(@RequestBody Map<String, Object> requestBody){
        String email = (String) requestBody.get("email");;
        return userSevice.forgetPassword(email);
    }
    @PutMapping("/reset-password/{token}")
    public ResponseEntity resetPassword(@RequestBody Map<String, Object> requestBody,@PathVariable String token){
        String password = (String) requestBody.get("password");;
        return userSevice.resetPassword(password,token);
    }
}
