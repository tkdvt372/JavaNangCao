package com.dvt.coursesweb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dvt.coursesweb.model.Course;
import com.dvt.coursesweb.model.User;
import com.dvt.coursesweb.model.submodel.Avatar;
import com.dvt.coursesweb.model.submodel.Playlist;
import com.dvt.coursesweb.repository.CourseRepository;
import com.dvt.coursesweb.repository.UserReposiroty;
import com.dvt.coursesweb.ultis.ErrorHandler;
import com.dvt.coursesweb.ultis.SendEmail;
import com.dvt.coursesweb.ultis.SendToken;
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
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserSevice {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Autowired
    UserReposiroty userReposiroty;
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    CourseRepository courseRepository;


    public ResponseEntity Register(HttpServletResponse res, MultipartFile file, String name, String email, String password) throws Exception {
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
            for (User user : users
            ) {
                if (user.getEmail().equals(email)) {
                    SendToken.sendToken(res, user);
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký thành công");
            response.put("user", temp);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đăng ký thất bại");
            response.put("user", temp);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    //Login
    public ResponseEntity Login(HttpServletResponse res, String email, String password) throws Exception {
        List<User> users = userReposiroty.findAll();
        User userFind = new User();
        int dem = 0;
        for (User user : users
        ) {
            if (user.getEmail().equals(email)) {
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    return ErrorHandler.Log("Mật khẩu không chính xác", HttpStatus.BAD_REQUEST);
                } else {
                    userFind = user;
                    dem++;
                }
            }
        }
        if (dem == 0) {
            return ErrorHandler.Log("Tài khoản hoặc mật khẩu không chính xác", HttpStatus.BAD_REQUEST);
        } else {
            SendToken.sendToken(res, userFind);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Chào mừng bạn quay trở lại " + userFind.getName() + "");
            response.put("success", true);
            response.put("user", userFind);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    public ResponseEntity Logout(HttpServletResponse res) {
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

    public ResponseEntity GetMyProfile(HttpServletRequest request) {
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
                    User user = userReposiroty.findById(userId).get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("user", user);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }


    //Change password
    public ResponseEntity ChangePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        if (oldPassword == " " || newPassword == " ") {
            return ErrorHandler.Log("Vui lòng nhập đầy đủ thông tin", HttpStatus.BAD_REQUEST);
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
                    User user = userReposiroty.findById(userId).get();
                    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                        return ErrorHandler.Log("Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST);
                    } else {
                        user.setPassword(encodednewPassword);
                    }
                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Đổi mật khẩu thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Đổi mật khẩu thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity updateProfile(HttpServletRequest request, String name, String email) {
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
                    User user = userReposiroty.findById(userId).get();
                    if (name != " ") {
                        user.setName(name);
                    }
                    if (email != " ") {
                        user.setEmail(email);
                    }
                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Cập nhật hồ sơ thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Cập nhật hồ sơ  thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }


    public ResponseEntity updateProfilePicture(HttpServletRequest request, MultipartFile file) {
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
                    User user = userReposiroty.findById(userId).get();
                    try {
                        Map r = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));
                        Map e = cloudinary.uploader().destroy(user.getAvatar().getPublic_id(), ObjectUtils.asMap("resource_type", "image"));
                        Avatar avatar = new Avatar();
                        avatar.setPublic_id(r.get("public_id").toString());
                        avatar.setUrl(r.get("secure_url").toString());
                        user.setAvatar(avatar);
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Cập nhật hồ sơ thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Cập nhật hồ sơ  thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity RemoveFromPlaylist(HttpServletRequest request, String id) {
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
                    User user = userReposiroty.findById(userId).get();
                    boolean check = false;
                    int index = -1;
                    for (Playlist playlist : user.getPlaylist()
                    ) {
                        if (playlist.getCourse().equals(id)) {
                            check = true;
                            index = user.getPlaylist().indexOf(playlist);
                        }
                    }

                    if (check == true && index >= 0) {
                        List<Playlist> playlists = user.getPlaylist();
                        playlists.remove(index);
                        user.setPlaylist(playlists);
                    }
                    if (check == false) {
                        return ErrorHandler.Log("Khoá học không có trong danh sách yêu thích", HttpStatus.BAD_REQUEST);
                    }
                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Bỏ yêu thích thành công!");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Bỏ yêu thích thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }


    public ResponseEntity addToPlaylist(HttpServletRequest request, String id) {
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
                    User user = userReposiroty.findById(userId).get();
                    Course course = courseRepository.findById(id).get();

                    if (user.getPlaylist() == null) {
                        user.setPlaylist(new ArrayList<>());
                    }
                    for (Playlist playlist : user.getPlaylist()
                    ) {
                        if (playlist.getId().equals(course.getId())) {
                            return ErrorHandler.Log("Khoá học đã tồn tại", HttpStatus.BAD_REQUEST);
                        }
                    }
                    List<Playlist> playlists = user.getPlaylist();
                    Playlist temp = new Playlist();
                    temp.setCourse(course.getId());
                    temp.setPoster(course.getPoster().getUrl());
                    playlists.add(temp);
                    user.setPlaylist(playlists);

                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Thêm vào danh sách ưa thích thành công!");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Thêm vào danh sách ưa thích thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Cookie not found", HttpStatus.NOT_FOUND);
    }


    public ResponseEntity forgetPassword(String email) {
        List<User> users = userReposiroty.findAll();
        for (User user : users
        ) {
            if (user.getEmail().equals(email)) {
                try {
                    String resetToken = user.getResetToken();
                    userReposiroty.save(user);
                    String url = "http://localhost:3000/reset-password/" + resetToken + "";
                    String message = "Nhấn vào link để đặt lại mật khẩu: " + url + "\n.Nếu bạn không có yêu cầu gì hãy bỏ qua";
                    SendEmail.sendEmail(user.getEmail(), "ĐẶT LẠI MẬT KHẨU", message);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Reset Token đã được gửi đến" + user.getEmail() + "");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } catch (Exception e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Gửi Reset Token thất bại");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity("Email không liên kết với tài khoản nào!", HttpStatus.NOT_FOUND);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
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

    public ResponseEntity resetPassword(String password, String token) {
        String resetPasswordToken = generateHash(token);
        LocalDateTime currentTime = LocalDateTime.now();
        List<User> users = userReposiroty.findAll();
        for (User user : users
        ) {
            if(user.getResetPasswordExpire() != ""){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime time = LocalDateTime.parse(user.getResetPasswordExpire(), formatter);
                if (user.getResetPasswordToken().equals(resetPasswordToken) && (currentTime.isBefore(time))) {
                    String encodednewPassword = passwordEncoder.encode(password);
                    user.setPassword(encodednewPassword);
                    try {
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Đổi mật khẩu thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println(e.getStackTrace());
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Đổi mật khẩu thất bại");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }
        return new ResponseEntity("Token không đúng hoặc hết hạn!", HttpStatus.NOT_FOUND);
    }

}
