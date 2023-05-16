package com.dvt.coursesweb.service;

import com.dvt.coursesweb.ultis.ErrorHandler;
import com.dvt.coursesweb.ultis.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OtherService {

    public ResponseEntity courseRequest(String name,String email, String course){
        if(name == null || email == null || course == null || name == "" || email == "" || course == "" ){
            return ErrorHandler.Log("Vui lòng nhập đầy đủ thông tin",HttpStatus.BAD_REQUEST);
        }
        String to = "duongvantuan372@gmail.com";
        String subject = "Yêu cầu khoá học về DVT";
        String text = "Tôi là " + name+" và email của tôi là : " + email+". \nKhoá học tôi cần là: " +course+ "";
        try {
            SendEmail.sendEmail(to,subject,text);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Yêu cầu của bạn đã được gửi!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Yêu cầu gửi thất bại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity contact(String name,String email, String message){
        if(name == null || email == null || message == null || name == "" || email == "" || message == "" ){
            return ErrorHandler.Log("Vui lòng nhập đầy đủ thông tin",HttpStatus.BAD_REQUEST);
        }
        String to = "duongvantuan372@gmail.com";
        String subject = "Yêu cầu khoá học về DVT";
        String text = "Tôi là " + name+" và email của tôi là : " + email+". \nTin nhắn : " +message+ "";
        try {
            SendEmail.sendEmail(to,subject,text);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Yêu cầu của bạn đã được gửi!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Yêu cầu gửi thất bại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
