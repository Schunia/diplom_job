package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Role;
import com.alexandra.Astro.Models.Subscription;
import com.alexandra.Astro.Models.User;
import com.alexandra.Astro.Services.SubscriptionService;
import com.alexandra.Astro.WorkClasses.InteractionPhoto;
import com.alexandra.Astro.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alexandra.Astro.Services.UserService.UPLOAD_DIRECTORY;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    //private final GuideService guideService;
    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 3;
    private static final int[] PAGE_SIZES = { 3, 6, 9, 12 };

    @GetMapping("/admin_cabinet/{pageNum}")
    public String admin(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                        Model model, @PathVariable(name = "pageNum") int pageNum,
                        Principal principal,
                        @Param("sortField") String sortField,
                        @Param("sortDir") String sortDir) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Page<User> page = userService.listAll(pageNum, sortField, sortDir);
        List<User> allUsers = page.getContent();
        List<Subscription> allSubscriptions = subscriptionService.getActiveSubscriptions();

        List<User> teachers = new ArrayList<>();
        teachers.add(new User(-1L, "Выбор..."));
        teachers.addAll(userService.getTeachers());

        //model.addAttribute("users", userService.list());
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("teachers", teachers);
        model.addAttribute("roles", Role.values());
        model.addAttribute("subscriptions", allSubscriptions);

        model.addAttribute("users", allUsers);
        model.addAttribute("activePage", "admin");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "authorization/admin";
    }

    @PostMapping("/admin_cabinet/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        userService.banUser(id);
        return "redirect:/admin_cabinet/1?sortField=login&sortDir=asc";
    }

    @GetMapping("/admin_cabinet/user/delete/{id}")
    public String userDelete(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        userService.deleteUserById(id);
        return "redirect:" + referrer;
    }
    @GetMapping("/admin_cabinet/hideSubscription/{id}")
    public String hideSubscription(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        Subscription sub = subscriptionService.getSubscriptionById(id);
        sub.setActive(false);
        subscriptionService.insertSubscription(sub);
        return "redirect:" + referrer;
    }

    @GetMapping("/admin_cabinet/user/{userId}")
    public String getUser(Model model, @PathVariable("userId") Long id) {
        User user = userService.getUserById(id);
        String photo = UPLOAD_DIRECTORY + user.getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(user.getTeacherId() != null){

        } else model.addAttribute("guide", "");

        model.addAttribute("user", user);
        model.addAttribute("photo", photo);
        return "authorization/personal_cabinet";
    }
}