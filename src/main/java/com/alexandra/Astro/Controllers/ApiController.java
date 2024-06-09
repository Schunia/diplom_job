package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Subscription;
import com.alexandra.Astro.Models.User;
import com.alexandra.Astro.Services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.FileTypeMap;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api", produces="application/json")
@CrossOrigin(origins="*")
public class ApiController {
    private CategoryService categoryService;
    private ObservationPhotoService observationPhotoService;
    private ArticlePhotoService articlePhotoService;

    public static String OBSERVATION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Observations\\";
    public static String ARTICLE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Articles\\";

    @Autowired
    private ServletContext servletContext;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private SubscriptionService subscriptionService;

    @Autowired
    public void setService(CategoryService categoryService, ArticlePhotoService articlePhotoService,
                           SubscriptionService subscriptionService, ObservationPhotoService observationPhotoService){
        this.categoryService = categoryService;
        this.articlePhotoService = articlePhotoService;
        this.observationPhotoService = observationPhotoService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/admin/user/{id}/changeRole/{role}")
    public String userEdit(@PathVariable Long id, @PathVariable String role) {
        User user = userService.getUserById(id);

        userService.changeUserRole(user, role);
        return "true";
    }

    @GetMapping("/admin/user/{id}/changeTeacher/{teacherId}")
    public String userEdit(@PathVariable Long id, @PathVariable Long teacherId) {
        User user = userService.getUserById(id);

        if(teacherId == -1L)
            user.setTeacherId(null);
        else
            user.setTeacherId(teacherId);
        userService.editUser(user);
        return "true";
    }

    @GetMapping("/user/{email}/{password}")
    public ObjectNode userLogin(@PathVariable String email, @PathVariable String password) {
        User user = userService.getUserByEmail(email);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();

        if(passwordEncoder.matches(password, user.getPassword())){
            objectNode.put("id", user.getId());
            objectNode.put("active", false);
            objectNode.put("email", user.getEmail());
            objectNode.put("password", user.getPassword());

        }

        return objectNode;
    }

    @GetMapping("/auth/login")
    public String login(Principal principal, Model model) {

        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "authorization/log_in";
    }
}
