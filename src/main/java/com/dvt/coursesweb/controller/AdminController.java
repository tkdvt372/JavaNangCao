package com.dvt.coursesweb.controller;

import com.dvt.coursesweb.service.AdminService;
import com.dvt.coursesweb.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/v1")
public class AdminController {
    @Autowired
    AdminService adminService;


    //Get all users
    @GetMapping("/admin/users")
    @CrossOrigin
    public ResponseEntity getAllUsers(){

        return adminService.getAllUsers();
    }


    //Change role a user
    @PutMapping("/admin/user/{id}")
    public ResponseEntity updateUserRole(@PathVariable String id){
        return adminService.updateUserRole(id);
    }

    //Delete a user
    @DeleteMapping("/admin/user/{id}")
    public ResponseEntity deleteUser(@PathVariable String id){
        return  adminService.deleteUser(id);
    }

    //Get dashboard admin
    @GetMapping("/admin/stats")
    public ResponseEntity getDashboardStats(){
        return adminService.getDashboardStats();
    }
}
