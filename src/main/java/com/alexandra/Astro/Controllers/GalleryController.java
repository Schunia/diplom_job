package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.ArticlePhoto;
import com.alexandra.Astro.Models.Observation;
import com.alexandra.Astro.Models.ObservationPhoto;
import com.alexandra.Astro.Models.Photo;
import com.alexandra.Astro.Services.*;
import com.alexandra.Astro.WorkClasses.InteractionPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GalleryController {

    private ObservationPhotoService observationPhotoService;
    private ArticlePhotoService articlePhotoService;

    public static String OBSERVATION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Observations\\";
    public static String ARTICLE_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Articles\\";

    @Autowired
    public void setServices(ArticlePhotoService articlePhotoService, ObservationPhotoService observationPhotoService){
        this.articlePhotoService = articlePhotoService;
        this.observationPhotoService = observationPhotoService;
    }

    @GetMapping("/gallery")
    public String getAll(Model model, Principal principal) {
        try {

            List<ObservationPhoto> observationPhotos = observationPhotoService.getAllObservationPhotos();
            List<ArticlePhoto> articlePhotos = articlePhotoService.getAllArticlePhotos();
            List<Photo> photos = new ArrayList<>();

            observationPhotos.forEach(item -> {
                try {
                    photos.add(new Photo("Observation_" + item.getId(),
                            InteractionPhoto.getPhoto(OBSERVATION_UPLOAD_DIRECTORY + item.getPathPhoto()),
                            item.getObservation().getUser().getLogin(),
                            item.getObservation().getObject()));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            articlePhotos.forEach(item -> {
                try {
                    photos.add(new Photo("Article_" + item.getId(),
                            InteractionPhoto.getPhoto(ARTICLE_UPLOAD_DIRECTORY + item.getPathPhoto()),
                            item.getArticle().getAuthor(),
                            item.getArticle().getTitle()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });


            model.addAttribute("photos", photos);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "gallery/gallery";
    }

    @GetMapping("/gallery/delete/{id}")
    public String deletePhotoById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") String id){

        String type = id.split("_")[0];
        Long item_id = Long.decode(id.split("_")[0]);

        switch(type){
            case "Article" :
                InteractionPhoto.deletePhoto("Articles\\" + articlePhotoService.getArticlePhotoById(item_id).getPathPhoto());
                articlePhotoService.deleteArticlePhotoById(item_id);
                break;
            case "Observation" :
                InteractionPhoto.deletePhoto("Observations\\" + observationPhotoService.getObservationPhotoById(item_id).getPathPhoto());
                observationPhotoService.deleteObservationPhotoById(item_id);
                break;
        }

        return "redirect:" + referrer;
    }
}
