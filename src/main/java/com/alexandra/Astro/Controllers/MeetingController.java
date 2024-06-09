package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Meeting;
import com.alexandra.Astro.Models.News;
import com.alexandra.Astro.Models.User;
import com.alexandra.Astro.Models.UserElectedMeeting;
import com.alexandra.Astro.Repositories.UserElectedMeetingRepository;
import com.alexandra.Astro.Services.MeetingService;
import com.alexandra.Astro.Services.UserService;
import com.alexandra.Astro.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
public class MeetingController {

    public static String MEETINGS_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Meetings\\";

    private UserService userService;
    private MeetingService meetingService;
    private final UserElectedMeetingRepository userElectedMeetingRepository;
    public MeetingController(UserElectedMeetingRepository userElectedMeetingRepository) {
        this.userElectedMeetingRepository = userElectedMeetingRepository;
    }

    @Autowired
    public void setMeetingService(MeetingService meetingService, UserService userService){
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @GetMapping("/meetings")
    public String meetingsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 Model model, @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "Все") String filter,
                                 @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
                                 @RequestParam(defaultValue = "title") String sortField,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 @RequestParam(defaultValue = "list") String scheme,
                                 Principal principal) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("user", user);
        }

        if (filter != null) {
            model.addAttribute("filter", filter);
        }

        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            Page<Meeting> pageMeetings = meetingService.listAll(page, size, filter, keyword,sortField, sortDir);
            List<Meeting> meetings = pageMeetings.getContent();

            for (var item : meetings) {
                try {
                    item.setPathPhoto(InteractionPhoto.getPhoto(MEETINGS_UPLOAD_DIRECTORY + item.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            List<String> statuses = new ArrayList<>();
            statuses.add("Все");
            statuses.add("Активная");
            statuses.add("Ожидает");
            statuses.add("Архив");

            model.addAttribute("meetings", meetings);
            model.addAttribute("activePage", "meetings");
            model.addAttribute("statuses", statuses);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageMeetings.getTotalElements());
            model.addAttribute("totalPages", pageMeetings.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "meetings/meetings";
    }

    @GetMapping("/meetings/create")
    public String createMeeting(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Model model) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        model.addAttribute("meeting", new Meeting());
        return "meetings/insert";
    }

    @PostMapping("/meetings/addingMeeting")
    public String insertMeeting(Meeting meeting, @RequestParam MultipartFile image) throws IOException {

        meeting.setStatus("Активная");

        String fileName = "meetings_" + meeting.getTitle() + "_" +
                (meetingService.getAllMeetings().size()) +
                image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4);

        Path fileNameAndPath = Paths.get(MEETINGS_UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        meeting.setPathPhoto(fileName);
        meetingService.insertMeeting(meeting);

        return "redirect:/meetings";
    }

    @GetMapping("/meetings/edit/{id}")
    public String editMeeting(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        String photo = MEETINGS_UPLOAD_DIRECTORY + meetingService.getMeetingById(id).getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> statuses = new ArrayList<>();
        statuses.add("Активная");
        statuses.add("Ожидает");
        statuses.add("Архив");


        model.addAttribute("photo", photo);

        Meeting meeting = meetingService.getMeetingById(id);
        model.addAttribute("meeting", meeting);
        model.addAttribute("statuses", statuses);

        return "meetings/edit";
    }

    @PostMapping("/meetings/editMeeting")
    public String editMeeting(Meeting meeting, @RequestParam MultipartFile image) throws IOException {

        if (image.getOriginalFilename() != "") {

            String fileName = "meetings_" + meeting.getTitle() + "_" +
                    (meetingService.getAllMeetings().size()) +
                    image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4);
            Path fileNameAndPath = Paths.get(MEETINGS_UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            meeting.setPathPhoto(fileName);

        }

        meetingService.insertMeeting(meeting);

        return "redirect:/meetings";
    }

    @GetMapping("/meetings/delete/{id}")
    public String deleteMeetingsById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        Meeting meeting = meetingService.getMeetingById(id);

        String path;
        path = "Meetings\\" + meeting.getPathPhoto();
        InteractionPhoto.deletePhoto(path);

        meetingService.deleteMeetingById(id);
        return "redirect:" + referrer;
    }

    @PostMapping("/meetings/{meetingId}/elect/{locate}")
    public String electMeeting(Meeting meeting, Principal principal, @PathVariable("locate") String locate,
                                 @PathVariable("meetingId") Long meetingId) throws IOException {
        User user = userService.getUserByPrincipal(principal);
        Set<UserElectedMeeting> elected = user.getUserElectedMeetings();

        UserElectedMeeting check = new UserElectedMeeting(user, meetingService.getMeetingById(meetingId));

        if (elected.contains(check)){
            check = userElectedMeetingRepository.findByUserIdAndMeetingId(user.getId(), meetingId);
            elected.remove(check);
            userElectedMeetingRepository.deleteById(check.getId());
        } else {
            elected.add(check);
        }
        user.setUserElectedMeetings(elected);
        userService.editUser(user);

        if(locate.equals("meetings"))
            return "redirect:/meetings";
        else
            return "redirect:/profile/elected";
    }
}
