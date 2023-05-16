package com.dvt.coursesweb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dvt.coursesweb.model.Course;
import com.dvt.coursesweb.model.Stat;
import com.dvt.coursesweb.model.User;
import com.dvt.coursesweb.repository.CourseRepository;
import com.dvt.coursesweb.repository.StatRepository;
import com.dvt.coursesweb.repository.UserReposiroty;
import com.dvt.coursesweb.ultis.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

@Component
public class AdminService {
    @Autowired
    Cloudinary cloudinary;
    @Autowired
    UserReposiroty userReposiroty;
    @Autowired
    StatRepository statRepository;
    @Autowired
    CourseRepository courseRepository;

    public ResponseEntity getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("users", userReposiroty.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity updateUserRole(String id) {
        User user = userReposiroty.findById(id).get();
        if (user == null) {
            return ErrorHandler.Log("Không tìm thấy tài khoản của bạn", HttpStatus.NOT_FOUND);
        }
        if (user.getRole().equals("user")) {
            user.setRole("admin");
        } else {
            user.setRole("user");
        }

        try {
            userReposiroty.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thay đổi thành công!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Thay đổi thất bại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity deleteUser(String id) {
        User user = userReposiroty.findById(id).get();
        if (user == null) {
            return ErrorHandler.Log("Không tìm thấy tài khoản của bạn", HttpStatus.NOT_FOUND);
        }
        try {
            Map r = cloudinary.uploader().destroy(user.getAvatar().getPublic_id(), ObjectUtils.asMap("resource_type", "image"));
            userReposiroty.delete(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xoá tài khoản thành công!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Xoá tài khoản thất bại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity getDashboardStats() {
        List<Stat> stats = statRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().limit(12).toList();
        List<Stat> statsData = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) {
            statsData.add(0, stats.get(i));
        }
        int requiredSize = 12 - stats.size();

        for (int i = 0; i < requiredSize; i++) {
            Stat temp = new Stat();
            temp.setUsers(0);
            temp.setViews(0);
            temp.setSubscription(0);
            statsData.add(0, temp);
        }
        int usersCount = statsData.get(11).getUsers();
        int subscriptionCount = statsData.get(11).getSubscription();
        int viewsCount = statsData.get(11).getViews();

        double usersPercentage = 0,
                viewsPercentage = 0,
                subscriptionPercentage = 0;

        boolean usersProfit = true,
                viewsProfit = true,
                subscriptionProfit = true;


        if (statsData.get(10).getUsers() == 0) {
            usersPercentage = usersCount * 100;
        }
        if (statsData.get(10).getViews() == 0) {
            viewsPercentage = viewsCount * 100;
        }
        if (statsData.get(10).getSubscription() == 0) {
            subscriptionPercentage = subscriptionCount * 100;
        } else {
            Stat temp = new Stat();
            temp.setUsers(statsData.get(11).getUsers() - statsData.get(10).getUsers());
            temp.setViews(statsData.get(11).getViews() - statsData.get(10).getViews());
            temp.setSubscription(statsData.get(11).getSubscription() - statsData.get(10).getSubscription());
            usersPercentage = Math.round(((double) temp.getUsers() / statsData.get(10).getUsers()) * 100 * 100.0) / 100.0;
            viewsPercentage = Math.round(((double) temp.getViews() / statsData.get(10).getViews()) * 100 * 100.0) / 100.0;
            subscriptionPercentage = Math.round(((double) temp.getSubscription() / statsData.get(10).getSubscription()) * 100 * 100.0) / 100.0;

            if (usersPercentage < 0) usersProfit = false;
            if (viewsPercentage < 0) viewsProfit = false;
            if (subscriptionPercentage < 0) subscriptionProfit = false;

        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", statsData);
        response.put("usersCount", usersCount);
        response.put("subscriptionCount", subscriptionCount);
        response.put("viewsCount", viewsCount);
        response.put("usersPercentage", usersPercentage);
        response.put("viewsPercentage", viewsPercentage);
        response.put("subscriptionPercentage", subscriptionPercentage);
        response.put("usersProfit", usersProfit);
        response.put("viewsProfit", viewsProfit);
        response.put("subscriptionProfit", subscriptionProfit);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Scheduled(cron = "0 * * * * *")
    public void updateStatsScheduled() {
        updateStats();
    }

    public void updateStats() {
        List<Stat> stats = statRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().limit(1).toList();
        Stat latestStats = stats.get(0);
        List<User> users = userReposiroty.findAll();
        List<User> usersActive = new ArrayList<>();

        for (User user : users
        ) {
            if (user.getSubscription() != null) {
                if (user.getSubscription().getStatus().equals("active")) {
                    usersActive.add(user);
                }
            }

        }
        List<Course> courses = courseRepository.findAll();
        int views = 0;
        for (Course course : courses
        ) {
            views += course.getViews();
        }
        int totalUsers = userReposiroty.findAll().size();


        latestStats.setUsers(totalUsers);
        latestStats.setSubscription(usersActive.size());
        latestStats.setViews(views);
        latestStats.setCreatedAt(new Date());

        statRepository.save(latestStats);
    }

}
