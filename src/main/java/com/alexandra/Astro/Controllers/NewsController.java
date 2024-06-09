package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.*;
import com.alexandra.Astro.Repositories.MeetingRepository;
import com.alexandra.Astro.Repositories.NewsRepository;
import com.alexandra.Astro.Services.*;
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

@Controller
public class NewsController {

    public static String NEWS_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\News\\";

    private NewsService newsService;
    private UserService userService;
    private MeetingService meetingService;
    private final NewsRepository newsRepository;
    private final MeetingRepository meetingRepository;

    public NewsController(NewsRepository newsRepository,
                          MeetingRepository meetingRepository) {
        this.newsRepository = newsRepository;
        this.meetingRepository = meetingRepository;
    }

    @Autowired
    public void setServices(NewsService newsService, UserService userService, MeetingService meetingService){
        this.newsService = newsService;
        this.userService = userService;
        this.meetingService = meetingService;
    }

    @GetMapping("/news")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
                         @RequestParam(defaultValue = "title") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
                         @RequestParam(defaultValue = "list") String scheme) {
        try {

            Page<News> pageNews = newsService.listAll(page, size, sortField, sortDir);
            List<News> news = pageNews.getContent();

            if(principal != null){
                User user = userService.getUserByPrincipal(principal);
                model.addAttribute("user", user);
            }

            for (var item : news) {
                try {
                    item.setPathPhoto(InteractionPhoto.getPhoto(NEWS_UPLOAD_DIRECTORY + item.getPathPhoto()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            model.addAttribute("news", news);
            model.addAttribute("activePage", "news");

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageNews.getTotalElements());
            model.addAttribute("totalPages", pageNews.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "news/news";
    }

    @GetMapping("/news/create")
    public String createNews(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Model model) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        List<Meeting> meetings = new ArrayList<>();
        meetings.add(new Meeting("Нет"));
        meetings.addAll(meetingService.getActualMeetings());

        model.addAttribute("news", new News());
        model.addAttribute("meetings", meetings);
        return "news/insert";
    }

    @PostMapping("/news/addingNews")
    public String insertNews(News news, @RequestParam MultipartFile image) throws IOException {

        if(news.getMeeting().getTitle().equals("Нет"))
            news.setMeeting(null);

        String fileName = "news_" + news.getTitle() + "_" +
                (newsService.getAllNews().size() + 1) +
                image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4);

        Path fileNameAndPath = Paths.get(NEWS_UPLOAD_DIRECTORY, fileName);
        try {
            Files.write(fileNameAndPath, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Date date = new Date();
        news.setDate(date);

        news.setPathPhoto(fileName);
        newsService.insertNews(news);

        return "redirect:/news";
    }

    @GetMapping("/news/edit/{id}")
    public String editNews(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        String photo = NEWS_UPLOAD_DIRECTORY + newsService.getNewsById(id).getPathPhoto();
        try {
            photo = InteractionPhoto.getPhoto(photo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Meeting> meetings = new ArrayList<>();
        meetings.add(new Meeting("Нет"));
        meetings.addAll(meetingService.getActualMeetings());

        model.addAttribute("photo", photo);

        News news = newsService.getNewsById(id);
        model.addAttribute("news", news);
        model.addAttribute("meetings", meetings);


        return "news/edit";
    }

    @PostMapping("/news/editNews")
    public String editNews(News news, @RequestParam MultipartFile image) throws IOException {

        if (image.getOriginalFilename() != "") {

            String fileName = "news_" + news.getTitle() + "_" +
                    (newsService.getAllNews().size() + 1) +
                    image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4);
                Path fileNameAndPath = Paths.get(NEWS_UPLOAD_DIRECTORY, fileName);
                try {
                    Files.write(fileNameAndPath, image.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            news.setPathPhoto(fileName);

        }

        newsService.insertNews(news);

        return "redirect:/news";
    }

    @GetMapping("/news/delete/{id}")
    public String deleteNewsById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        News news = newsService.getNewsById(id);

        String path;
        path = "News\\" + news.getPathPhoto();
        InteractionPhoto.deletePhoto(path);

        newsService.deleteNewsById(id);
        return "redirect:" + referrer;
    }
}
