package com.dvt.coursesweb.controller;

import com.dvt.coursesweb.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
@RequestMapping("/api/v1")
public class OtherController {
    @Autowired
    OtherService otherService;


    //Request a course
    @PostMapping("/course-request")
    public ResponseEntity courseRequest(@RequestBody Map<String, Object> requestBody){
        String name = (String) requestBody.get("name");
        String email = (String) requestBody.get("email");
        String course = (String) requestBody.get("course");
        return otherService.courseRequest(name,email,course);
    }

    //Contact
    @PostMapping("/contact")
    public ResponseEntity contact(@RequestBody Map<String, Object> requestBody){
        String name = (String) requestBody.get("name");
        String email = (String) requestBody.get("email");
        String message = (String) requestBody.get("message");
        return otherService.contact(name,email,message);
    }
}
