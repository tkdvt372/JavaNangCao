package com.dvt.coursesweb.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dvt.coursesweb.model.Course;
import com.dvt.coursesweb.model.submodel.Lecture;
import com.dvt.coursesweb.model.submodel.Poster;
import com.dvt.coursesweb.model.submodel.Video;
import com.dvt.coursesweb.repository.CourseRepository;
import com.dvt.coursesweb.repository.SearchRepository;
import com.dvt.coursesweb.ultis.ErrorHandler;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class CourseController {
    @Autowired
    CourseRepository repo;
    @Autowired
    SearchRepository srepo;
    @Autowired
    Cloudinary cloudinary;

    @ApiIgnore
    @RequestMapping(value = "/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    //Gat all courses
    @GetMapping("/courses")
    @CrossOrigin
    public ResponseEntity getAllCourses(@RequestParam(value = "title", required = false) String title, @RequestParam(value = "category", required = false) String category) {
        if (title == null && category == null) {
            List<Course> listCourses = repo.findAll();
            List<Course> output = listCourses.stream()
                    .map(course -> new Course(
                            course.getTitle()
                            , course.getDescription()
                            , course.getPoster()
                            , course.getViews()
                            , course.getNumOfVideos()
                            , course.getCategory()
                            , course.getCreatedBy()
                            , course.getCreatedAt()
                    )).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courses", output);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity(srepo.getAllCourse(title, category), HttpStatus.OK);
    }


    //Create a course
    @PostMapping("/create-course")
    public ResponseEntity<Object> createCourse(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("category") String category, @RequestParam("createdBy") String createdBy) throws IOException {
        Map r = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        Course temp = new Course();
        temp.setTitle(title);
        temp.setDescription(description);
        temp.setCategory(category);
        temp.setCreatedBy(createdBy);
        Poster a = new Poster(r.get("public_id").toString(), r.get("secure_url").toString());
        temp.setPoster(a);
        repo.save(temp);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tạo khoá học thành công bạn có thể thêm bài giảng");
        response.put("success", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Get full lectures of a course
    @GetMapping("/course/{id}")
    public ResponseEntity getCourseLecture(@PathVariable ObjectId id) {
        Optional<Course> course = srepo.singleCourse(id);
        if (course.isEmpty()) {
            return ErrorHandler.Log("Không tìm thấy khoá học",HttpStatus.NOT_FOUND);
        }
        int views = course.get().getViews()+1;
        course.get().setViews(views);
        repo.save(course.get());
        return new ResponseEntity<>(course.get().getLectures(), HttpStatus.OK);
    }


    //Adding a lecture to a course
    @PostMapping("/course/{id}")
    public ResponseEntity addLecture(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description, @PathVariable ObjectId id) throws IOException {
        Optional<Course> temp = srepo.singleCourse(id);
        if (temp.isEmpty()) {
            return ErrorHandler.Log("Không tìm thấy khoá học",HttpStatus.NOT_FOUND);
        }
        Course course = temp.get();
        if (course.getLectures() == null) {
            Map r = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
            Lecture lecture = new Lecture();
            lecture.setTitle(title);
            lecture.setDescription(description);
            Video video = new Video();
            video.setUrl(r.get("secure_url").toString());
            video.setPublic_id(r.get("public_id").toString());
            lecture.setVideo(video);
            List<Lecture> lectures = new ArrayList<>();
            lectures.add(lecture);
            course.setLectures(lectures);
            course.setNumOfVideos(course.getLectures().size());
            try {
                repo.save(course);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Thêm bài giảng thành công");
                response.put("success", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return ErrorHandler.Log("Thêm bài giảng thất bại",HttpStatus.BAD_REQUEST);
            }
        } else {
            Map r = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "video"));
            Lecture lecture = new Lecture();
            lecture.setTitle(title);
            lecture.setDescription(description);
            Video video = new Video();
            video.setUrl(r.get("secure_url").toString());
            video.setPublic_id(r.get("public_id").toString());
            lecture.setVideo(video);
            List<Lecture> lectures = course.getLectures();
            lectures.add(lecture);
            course.setLectures(lectures);
            course.setNumOfVideos(course.getLectures().size());
            try {
                repo.save(course);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Thêm bài giảng thành công");
                response.put("success", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return ErrorHandler.Log("Thêm khoá học thất bại",HttpStatus.BAD_REQUEST);
            }
        }
    }

    //Delete course
    @DeleteMapping("/course/{id}")
    public ResponseEntity deleteCourse(@PathVariable ObjectId id) throws IOException {
        Optional<Course> temp = srepo.singleCourse(id);
        if (temp.isEmpty()) {
            return ErrorHandler.Log("Không tìm thấy khoá học",HttpStatus.NOT_FOUND);
        }
        Course course = temp.get();
        cloudinary.uploader().destroy(course.getPoster().getPublic_id(), ObjectUtils.asMap("resource_type", "image"));
        if (course.getLectures() != null) {

            for (var item : course.getLectures()
            ) {
                cloudinary.uploader().destroy(item.getVideo().getPublic_id(), ObjectUtils.asMap("resource_type", "video"));
            }
        }
        try {
            repo.deleteById(String.valueOf(id));
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xoá khoá học thành công");
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorHandler.Log("Xoá khoá học thất bại",HttpStatus.BAD_REQUEST);
        }
    }


    //Delete lecture
    @DeleteMapping("/lecture")
    public ResponseEntity deleteLecture(@RequestParam("courseId") ObjectId courseId, @RequestParam("lectureId") ObjectId lectureId) throws IOException {
        Optional<Course> temp = srepo.singleCourse(courseId);
        if (temp.isEmpty()) {
            return ErrorHandler.Log("Không tìm thấy khoá học",HttpStatus.NOT_FOUND);
        }
        Course course = temp.get();
        List<Lecture> lectures = course.getLectures();
        if (lectures != null) {

            Iterator<Lecture> iterator = lectures.iterator();
            while (iterator.hasNext()) {
                Lecture item = iterator.next();
                if (lectureId.toString().equals(item.getId().toString())) {
                    cloudinary.uploader().destroy(item.getVideo().getPublic_id(), ObjectUtils.asMap("resource_type", "video"));
                    iterator.remove();
                }
            }
            course.setLectures(lectures);
            course.setNumOfVideos(course.getLectures().size());
        }
        try {

            repo.save(course);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xoá bài giảng thành công");
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ErrorHandler.Log("Xoá bài giảng thất bại",HttpStatus.BAD_REQUEST);
        }
    }
}
