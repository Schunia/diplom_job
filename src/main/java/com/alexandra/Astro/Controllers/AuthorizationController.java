package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.*;
import com.alexandra.Astro.Services.MeetingService;
import com.alexandra.Astro.WorkClasses.InteractionPhoto;
import com.alexandra.Astro.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorizationController {
    private final UserService userService;
    private final MeetingService meetingService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Users\\";
    public static String MEETINGS_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Meetings\\";


    @GetMapping("/log_in")
    public String login(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                        Principal principal, Model model) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/log_in";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.getUserByPrincipal(principal);
        String photo = UPLOAD_DIRECTORY + user.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("photo", photo);

        if (user.getTeacherId() != null) {
            User teacher = userService.getUserById(user.getTeacherId());

            photo = UPLOAD_DIRECTORY + teacher.getPathPhoto();
            try {
                photo = InteractionPhoto.getPhoto(photo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            model.addAttribute("teacher", teacher);
            model.addAttribute("teacherPhoto", photo);

        } else {
            model.addAttribute("teacher", "");

            if(user.getRoles().contains(Role.ROLE_TEACHER)){
                List<User> students = new ArrayList<>();
                students.addAll(userService.getStudents(user.getId()));
                model.addAttribute("students", students);
            }

        }


        model.addAttribute("user", user);

        return "authorization/personal_cabinet";
    }

    @GetMapping("/profile/elected")
    public String elected(Principal principal, Model model) {
        User user = userService.getUserByPrincipal(principal);

        List<Meeting> meetings = meetingService.getElectedMeetings(user.getId());

        for (var meeting : meetings) {
            try {
                meeting.setPathPhoto(InteractionPhoto.getPhoto(MEETINGS_UPLOAD_DIRECTORY + meeting.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("meetings", meetings);

        return "authorization/elected";
    }

    @PostMapping("/profile/edit")
    public String editProfile(User user, @RequestParam("image") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            String fileName = user.getLogin() + "_" + user.getId() + "_" +
                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            user.setPathPhoto(fileName);
        }
        userService.editUser(user);
        return "redirect:/profile";
    }

    @GetMapping("/sign_up")
    public String registration(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("errorMessage", "");
//        return "authorization/registration";
        return "authorization/sign_up";
    }

    @PostMapping("/sign_up")
    public String createUser(User user, Model model, @RequestParam("image") MultipartFile file) throws IOException {
        String fileName = user.getLogin() + "_" +
                file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);

        user.setPathPhoto(fileName);

        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
//            return "authorization/registration";
            return "authorization/sign_up";
        }

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/log_in";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("userByPrincipal", userService.getUserByPrincipal(principal));
        return "authorization/user_info";
    }
}