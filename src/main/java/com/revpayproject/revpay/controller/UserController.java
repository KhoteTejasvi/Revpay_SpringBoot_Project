package com.revpayproject.revpay.controller;

import com.revpayproject.revpay.dto.CreateBusinessDto;
import com.revpayproject.revpay.dto.NotificationPreferenceDto;
import com.revpayproject.revpay.dto.SetPinDto;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.UserRepository;
import com.revpayproject.revpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.revpayproject.revpay.dto.SecurityQuestionDto;
import com.revpayproject.revpay.service.SecurityQuestionService;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.File;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityQuestionService securityQuestionService;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/notification-preferences")
    public String updatePreferences(
            @RequestBody NotificationPreferenceDto dto) {

        return userService.updateNotificationPreferences(
                getLoggedInUser().getEmail(),
                dto
        );
    }

    @PostMapping("/upgrade-business")
    public String upgradeToBusiness(
            @RequestBody CreateBusinessDto dto) {

        return userService.upgradeToBusiness(
                getLoggedInUser().getEmail(),
                dto
        );
    }

    @PostMapping("/set-pin")
    public String setPin(@RequestBody SetPinDto dto) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.setTransactionPin(email, dto.getPin());
    }

    @GetMapping("/profile")
    public User getProfile() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/security-questions")
    public String updateSecurityQuestions(
            @RequestBody List<SecurityQuestionDto> dtos) {

        securityQuestionService.updateSecurityQuestions(
                getLoggedInUser(),
                dtos
        );

        return "Security questions updated successfully";
    }

    @PostMapping("/upload-profile-image")
    public String uploadProfileImage(
            @RequestParam("file") MultipartFile file) throws Exception {

        User user = getLoggedInUser();

        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = user.getId() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);

        Files.write(path, file.getBytes());

        user.setProfileImage(fileName);
        userRepository.save(user);

        return "Profile image uploaded successfully";
    }

    @GetMapping("/profile-image/{filename}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String filename) throws Exception {

        Path path = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}