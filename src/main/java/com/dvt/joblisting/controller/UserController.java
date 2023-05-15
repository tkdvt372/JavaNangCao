package com.dvt.joblisting.controller;

import com.dvt.joblisting.service.UserSevice;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserSevice userSevice;
    @GetMapping("/admin/users")
    public ResponseEntity getAllUsers(){

        return new ResponseEntity(userSevice.getAllUsers(), HttpStatus.OK);
    }

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

    @GetMapping("/me")
    public ResponseEntity getMyProfile(HttpServletRequest request) {
            return userSevice.GetMyProfile(request);
    }

    @PutMapping("/change-password")
    public ResponseEntity changePassword(HttpServletRequest request,@RequestBody Map<String, Object> requestBody){
        String oldPassword = (String) requestBody.get("oldPassword");
        String newPassword = (String) requestBody.get("newPassword");
        return userSevice.ChangePassword(request,oldPassword,newPassword);
    }
}
