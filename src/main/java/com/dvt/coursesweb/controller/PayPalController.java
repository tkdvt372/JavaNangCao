package com.dvt.coursesweb.controller;

import com.dvt.coursesweb.model.User;
import com.dvt.coursesweb.model.submodel.Subscription;
import com.dvt.coursesweb.repository.UserReposiroty;
import com.dvt.coursesweb.service.PayPalService;
import com.dvt.coursesweb.ultis.ErrorHandler;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.base.rest.PayPalRESTException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/v1")
public class PayPalController {
    @Autowired
    PayPalService payPalService;
    @Autowired
    UserReposiroty userReposiroty;
    public static final String SUCCESS_URL = "/api/v1/success";
    public static final String CANCEL_URL = "/api/v1/cancel";

    @GetMapping("/subscription")
    public ResponseEntity payment(HttpServletRequest request, @RequestParam("total") String total, @RequestParam("title") String title, @RequestParam("sku") String sku) {
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
                        if (user.getRole() == "admin") {
                            return ErrorHandler.Log("Quản trị viên không thể đăng ký thành viên", HttpStatus.BAD_REQUEST);
                        }
                        Payment payment = payPalService.createPayment(total, title, sku, "http://localhost:8080" + CANCEL_URL,
                                "http://localhost:8080" + SUCCESS_URL);

                        for (Links link : payment.getLinks()) {
                            if (link.getRel().equals("approval_url")) {
                                Subscription subscription = new Subscription();
                                subscription.setId(payment.getId());
                                subscription.setStatus(payment.getState());
                                user.setSubscription(subscription);
                                userReposiroty.save(user);
                                Map<String, Object> response = new HashMap<>();
                                response.put("success", true);
                                response.put("url", link.getHref());
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }
                        }
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("url", "http://localhost:3000/subscribe");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);


                    } catch (PayPalRESTException e) {

                        e.printStackTrace();
                    }

                }
            }
        }
        return new ResponseEntity("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/subscribe/cancel")
    public ResponseEntity  cancelSubscription(HttpServletRequest request){
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
                        Subscription temp = new Subscription();
                        temp.setId(null);
                        temp.setStatus(null);
                        user.setSubscription(temp);
                        userReposiroty.save(user);
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Huỷ thành viên thành công");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
        return new ResponseEntity("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/success")
    public void successPay(HttpServletRequest request, HttpServletResponse response, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
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
                        Payment payment = payPalService.executePayment(paymentId, payerId);
                        if (payment.getState().equals("approved")) {
                            Subscription temp = new Subscription();
                            temp.setStatus("active");
                            temp.setCreatedTime(new Date());
                            user.setSubscription(temp);
                            userReposiroty.save(user);
                           response.sendRedirect("http://localhost:3000/courses");
                        }


                    } catch (PayPalRESTException e) {

                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }

}
