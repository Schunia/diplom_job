package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.*;
import com.alexandra.Astro.Services.ArticlePhotoService;
import com.alexandra.Astro.Services.ArticleService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class ArticleController {
    private ArticleService articleService;
    private UserService userService;
    private ArticlePhotoService articlePhotoService;

    public static String ARTICLE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Articles\\";

    @Autowired
    public void setServices(ArticleService articleService, UserService userService, ArticlePhotoService articlePhotoService) {
        this.articleService = articleService;
        this.userService = userService;
        this.articlePhotoService = articlePhotoService;
    }

    private void fillModelWithArticle(Model model, Article article) {
        model.addAttribute("article", article);
        model.addAttribute("selectedArticle", article);

    }

    @GetMapping("/articles")
    public String getAll(Model model, @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
        @RequestParam(defaultValue = "title") String sortField,
        @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
        @RequestParam(defaultValue = "card") String scheme) {

        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            Page<Article> pageArticles = articleService.listAll(page, size, keyword, sortField, sortDir, false);
            List<Article> articles = pageArticles.getContent();

            if(principal != null){
                User user = userService.getUserByPrincipal(principal);
                model.addAttribute("user", user);
            }

            //ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4, 6, 8, 10));

            for (var article : articles){
                for (var photo : article.getArticlePhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(ARTICLE_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            model.addAttribute("articles", articles);
            model.addAttribute("activePage", "articles");

            //model.addAttribute("arr", arr);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageArticles.getTotalElements());
            model.addAttribute("totalPages", pageArticles.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "articles/articles";
    }

    @GetMapping("/articles/create")
    public String createMark(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Model model) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        fillModelWithArticle(model, new Article());
        return "articles/insert";
    }

    @GetMapping("/articles/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {
        Article selectedArticle = articleService.getArticleById(id);

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        List<String> photos = new ArrayList<>();
        for (var photo : selectedArticle.getArticlePhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(ARTICLE_UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //List<Excursion> excursions = selectedGuide.getExcursions();
        model.addAttribute("selectedArticle", selectedArticle);
        model.addAttribute("photos", photos);
        //model.addAttribute("excursions", excursions);
        return "articles/details";
    }

    @GetMapping("/articles/edit/{id}")
    public String editPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Article selectedArticle = articleService.getArticleById(id);

        List<String> photos = new ArrayList<>();
        for (var photo : selectedArticle.getArticlePhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(ARTICLE_UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        fillModelWithArticle(model, selectedArticle);

        model.addAttribute("photos", photos);
        return "articles/edit";
    }

    @PostMapping("/articles/addingArticle")
    public String insertArticle(@ModelAttribute Article article, @RequestParam MultipartFile[] upload) {

        Date date = new Date();
        article.setDate(date);

        articleService.insertArticle(article);

        int i = 1;
        for (var item: upload){
            ArticlePhoto photo = new ArticlePhoto();
            photo.setArticle(article);
            String fileName = "article_" + article.getTitle() + "_" +
                    (articleService.getAllArticles().size() + 1) + "_" + i +
                    item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
            Path fileNameAndPath = Paths.get(ARTICLE_UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, item.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            photo.setPathPhoto(fileName);
            articlePhotoService.insertArticlePhoto(photo);
            i++;
        }
        return "redirect:/articles";
    }

    @PostMapping("/articles/editArticle")
    public String editArticle(Article article, @RequestParam MultipartFile[] upload, Principal principal) throws IOException {

//        if (!file.isEmpty()) {
//            User user = userService.getUserByPrincipal(principal);
////            String fileName = guide.getLastName() + "_" + guide.getTelNumber() +
////                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
//            String fileName = user.getLogin() + "_" + user.getId() + "_" +
//                    file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
//
//            Path fileNameAndPath = Paths.get(ARTICLE_UPLOAD_DIRECTORY, fileName);
//            try {
//                Files.write(fileNameAndPath, file.getBytes());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            article.setPathPhoto(fileName);
//        }
        if (upload[0].getOriginalFilename() != "") {

            String path;
            for (var photo : articleService.getArticleById(article.getId()).getArticlePhotos()) {
                path = "Articles\\" + photo.getPathPhoto();
                articlePhotoService.deleteArticlePhotoById(photo.getId());

                InteractionPhoto.deletePhoto(path);
            }

            List<ArticlePhoto> articlePhotos = articlePhotoService.getAllArticlePhotos();



            int i = 1;
            for (var item: upload){
                ArticlePhoto photo = new ArticlePhoto();
                photo.setArticle(article);
                String fileName = "article_" + article.getId() + "_" +
                        article.getTitle() + "_" + i +
                        item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
                Path fileNameAndPath = Paths.get(ARTICLE_UPLOAD_DIRECTORY, fileName);
                try {
                    Files.write(fileNameAndPath, item.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                photo.setPathPhoto(fileName);
                articlePhotoService.insertArticlePhoto(photo);
                i++;
            }
        }

        articleService.insertArticle(article);
        return "redirect:/profile";
    }

    @GetMapping("/articles/delete/{id}")
    public String deleteArticleById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                  @PathVariable("id") Long id) {
        Article article = articleService.getArticleById(id);

        String path;
        for (var photo: article.getArticlePhotos()){
            path = "Articles\\" + photo.getPathPhoto();
            InteractionPhoto.deletePhoto(path);
        }

        articleService.deleteArticleById(id);
        return "redirect:" + referrer;
    }



    @GetMapping("/lectures")
    public String getLectures(Model model, @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
                         @RequestParam(defaultValue = "title") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
                         @RequestParam(defaultValue = "card") String scheme) {

        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }

            Page<Article> pageArticles = articleService.listAll(page, size, keyword, sortField, sortDir, true);
            List<Article> articles = pageArticles.getContent();

            if(principal != null){
                User user = userService.getUserByPrincipal(principal);
                model.addAttribute("user", user);
            }

            //ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4, 6, 8, 10));

            for (var article : articles){
                for (var photo : article.getArticlePhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(ARTICLE_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            model.addAttribute("articles", articles);
            model.addAttribute("activePage", "lectures");

            //model.addAttribute("arr", arr);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageArticles.getTotalElements());
            model.addAttribute("totalPages", pageArticles.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }
        return "articles/lectures";
    }
}
