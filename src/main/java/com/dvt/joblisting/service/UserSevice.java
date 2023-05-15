package com.dvt.joblisting.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dvt.joblisting.model.User;
import com.dvt.joblisting.model.submodel.Avatar;
import com.dvt.joblisting.repository.UserReposiroty;
import com.dvt.joblisting.ultis.ErrorHandler;
import com.dvt.joblisting.ultis.SendToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserSevice {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Autowired
    UserReposiroty userReposiroty;
    @Autowired
    Cloudinary cloudinary;

    public List<User> getAllUsers(){
        return userReposiroty.findAll();
    }
    public ResponseEntity Register(HttpServletResponse res,MultipartFile file, String name, String email, String password) throws Exception {
        User temp = new User();
        temp.setName(name);
        temp.setEmail(email);
        String encodedPassword = passwordEncoder.encode(password);
        temp.setPassword(encodedPassword);
        Avatar avatar = new Avatar();
        Map r = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));
        avatar.setPublic_id(r.get("public_id").toString());
        avatar.setUrl(r.get("secure_url").toString());
        temp.setAvatar(avatar);
        try {
            userReposiroty.save(temp);
            List<User> users = userReposiroty.findAll();
            for (User user:users
                 ) {
                if(user.getEmail().equals(email)){
                    SendToken.sendToken(res,user);
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký thành công");
            response.put("user",temp);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đăng ký thất bại");
            response.put("user",temp);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    //Login
    public ResponseEntity Login(HttpServletResponse res,String email, String password) throws Exception {
        List<User> users = userReposiroty.findAll();
        User userFind = new User();
        int dem=0;
        for (User user:users
             ) {
            if(user.getEmail().equals(email)){
                if(!passwordEncoder.matches(password, user.getPassword())){
                    return ErrorHandler.Log("Mật khẩu không chính xác",HttpStatus.BAD_REQUEST);
                }else{
                    userFind = user;
                    dem++;
                }
            }
        }
        if(dem ==0){
            return ErrorHandler.Log("Tài khoản hoặc mật khẩu không chính xác",HttpStatus.BAD_REQUEST);
        }else{
            SendToken.sendToken(res,userFind);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Chào mừng bạn quay trở lại "+userFind.getName()+"");
            response.put("success", true);
            response.put("user",userFind);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    public ResponseEntity Logout(HttpServletResponse res){
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        res.addCookie(cookie);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng xuất thành công");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity GetMyProfile(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    String secret = "duongvantuanduongvantuanduongvantuanduongvantuan";

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(secret)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String userId = claims.getSubject();
                    Optional<User> userTemp = userReposiroty.findById(userId);
                    User user = userTemp.get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("user",user);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }


    //Change password
    public ResponseEntity ChangePassword(HttpServletRequest request,String oldPassword,String newPassword){
        if(oldPassword == " " || newPassword == " "){
            ErrorHandler.Log("Vui lòng nhập đầy đủ thông tin",HttpStatus.BAD_REQUEST);
        }
        String encodednewPassword = passwordEncoder.encode(newPassword);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    String secret = "duongvantuanduongvantuanduongvantuanduongvantuan";

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(secret)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String userId = claims.getSubject();
                    Optional<User> userTemp = userReposiroty.findById(userId);
                    User user = userTemp.get();
                    if(!passwordEncoder.matches(oldPassword,user.getPassword())){
                        return ErrorHandler.Log("Mật khẩu cũ không chính xác",HttpStatus.BAD_REQUEST);
                    }else{
                        user.setPassword(encodednewPassword);
                    }
                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message","Đổi mật khẩu thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }catch (Exception e){
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message","Đổi mật khẩu thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity updateProfile(HttpServletRequest request,String name,String email){
        return null;
    }
}
