package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.*;
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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ObservationController {
    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 3;
    private static final int[] PAGE_SIZES = { 3, 6, 9, 12 };

    public static String OBSERVATION_UPLOAD_DIRECTORY = System.getProperty("user.dir") + "\\Photos\\Observations\\";

    private ObservationService observationService;
    private CategoryService categoryService;
    private ObservationPhotoService observationPhotoService;
    private UserService userService;
//    private final ObservationRepository observationRepository;
//
//    public ObservationController(ObservationRepository observationRepository,
//                          UserElectedMarkRepository userElectedMarkRepository) {
//        this.observationRepository = observationRepository;
//        this.userElectedMarkRepository = userElectedMarkRepository;
//    }

    @Autowired
    public void setServices(ObservationService observationService, CategoryService categoryService,
                            UserService userService, ObservationPhotoService observationPhotoService){
        this.observationService = observationService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.observationPhotoService = observationPhotoService;
    }

    @GetMapping("/observations")
    public String getAll(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "Все") String filter,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
                         @RequestParam(defaultValue = "object") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
                         @RequestParam(defaultValue = "card") String scheme) {
        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }
            if (filter != null) {
                model.addAttribute("filter", filter);
            }

            Page<Observation> pageObservations = observationService.listAll(page, size, filter, keyword, sortField, sortDir);
            List<Observation> observations = pageObservations.getContent();

            if(principal != null){
                User user = userService.getUserByPrincipal(principal);
                model.addAttribute("user", user);
            }

            ArrayList<Long> list = new ArrayList<>();
            ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4, 6, 8, 10));

            for (var observation : observations){
                for (var photo : observation.getObservationPhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(OBSERVATION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            List<String> categories = new ArrayList<>();
            categories.add("Все");
            categoryService.getAllCategories().forEach(category -> {
                categories.add(category.getTitle());
            });

            model.addAttribute("observations", observations);
            model.addAttribute("categories", categories);
            model.addAttribute("activePage", "observations");

            model.addAttribute("arr", arr);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageObservations.getTotalElements());
            model.addAttribute("totalPages", pageObservations.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "observations/observations";
    }

    @GetMapping("/observations/create")
    public String createObservation(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Principal principal, Model model) {
        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

//        fillModelWithMark(model, new Mark(), new Schedule(),
//                new Schedule(), new Schedule(),
//                new Schedule(), new Schedule(),
//                new Schedule(), new Schedule());

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("member", user);
        }

        model.addAttribute("observation", new Observation());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "observations/insert";
    }

    @GetMapping("/observations/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Observation selectedObservation = observationService.getObservationById(id);
        List<String> photos = new ArrayList<>();
        for (var photo : selectedObservation.getObservationPhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(OBSERVATION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String date = "";

        model.addAttribute("selectedObservation", selectedObservation);
        model.addAttribute("photos", photos);
        model.addAttribute("str_date", date);
//        model.addAttribute("schedules", schedules);
        return "observations/details";
    }

    @PostMapping("/observations/addingObservation")
    public String insertObservation(Observation observation, Principal principal, @RequestParam MultipartFile[] upload) throws IOException {

        observation.setUser(userService.getUserByPrincipal(principal));
        observation.setStatus(Status.Unconfirmed);
        //observation.setDate(LocalDateTime.parse());
        observationService.insertObservation(observation);

        int i = 1;
        for (var item: upload){
            ObservationPhoto photo = new ObservationPhoto();
            photo.setObservation(observation);
            String fileName = "observation_" + observation.getObject() + "_" +
                    (observationService.getAllObservations().size() + 1) + "_" + i +
                    item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
            Path fileNameAndPath = Paths.get(OBSERVATION_UPLOAD_DIRECTORY, fileName);
            try {
                Files.write(fileNameAndPath, item.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            photo.setPathPhoto(fileName);
            observationPhotoService.insertObservationPhoto(photo);
            i++;
        }

//        Mark mrk = observationService.getMarkByTitleAndDescription(observation.getTitle(), observation.getDescription()).get();
//        Schedule schedule1 = new Schedule();
//        schedule1.setStart(s1_start);
//        schedule1.setEnd(s1_end);
//        schedule1.setDay(1);
//        schedule1.setMark(mrk);
//        scheduleService.insertSchedule(schedule1);

        return "redirect:/observations";
    }

    @GetMapping("/observations/edit/{id}")
    public String editObservation(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer, Principal principal,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        if(principal != null){
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("member", user);
        }

        Observation selectedObservation = observationService.getObservationById(id);

        List<String> photos = new ArrayList<>();
        for (var photo : selectedObservation.getObservationPhotos()){
            try {
                photos.add(InteractionPhoto.getPhoto(OBSERVATION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Dictionary<String, String> statuses = new Hashtable<>();
        statuses.put(Status.Confirmed.name(), Status.Confirmed.getTitle());
        statuses.put(Status.Unconfirmed.name(), Status.Unconfirmed.getTitle());

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", statuses);
        model.addAttribute("selectedObservation", selectedObservation);
        model.addAttribute("photos", photos);
        return "observations/edit";
    }

    @PostMapping("/observations/editObservation")
    public String editObservation(Observation observation, @RequestParam MultipartFile[] upload) throws IOException {

        if (upload[0].getOriginalFilename() != "") {

            String path;
            for (var photo : observationService.getObservationById(observation.getId()).getObservationPhotos()) {
                path = "Observations\\" + photo.getPathPhoto();
                observationPhotoService.deleteObservationPhotoById(photo.getId());

                InteractionPhoto.deletePhoto(path);
            }

            List<ObservationPhoto> observationPhotos = observationPhotoService.getAllObservationPhotos();

            int i = 1;
            for (var item: upload){
                ObservationPhoto photo = new ObservationPhoto();
                photo.setObservation(observation);
                String fileName = "observation_" + observation.getId() + "_" +
                        observation.getObject() + "_" + i +
                        item.getOriginalFilename().substring(item.getOriginalFilename().length()-4);
                Path fileNameAndPath = Paths.get(OBSERVATION_UPLOAD_DIRECTORY, fileName);
                try {
                    Files.write(fileNameAndPath, item.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                photo.setPathPhoto(fileName);
                observationPhotoService.insertObservationPhoto(photo);
                i++;
            }
        }

        observationService.insertObservation(observation);

//        Mark mrk = observationService.getMarkByTitleAndDescription(mark.getTitle(), mark.getDescription()).get();
//        List<Schedule> schedules = scheduleService.getSchedulesByMark(markService.getMarkById(mrk.getId()));
//
//        schedules.get(0).setStart(s1_start);
//        schedules.get(0).setEnd(s1_end);
//        schedules.get(0).setDay(1);
//        schedules.get(0).setMark(mrk);
//        scheduleService.insertSchedule(schedules.get(0));
        return "redirect:/observations";
    }

//    @DeleteMapping("/places/delete/{id}")
//    public String deleteMarkById(@PathVariable("id") Long id){
//        markService.deleteMarkById(id);
//        return "redirect:/marks/places";
//    }

    @GetMapping("/observations/delete/{id}")
    public String deleteObservById(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 @PathVariable("id") Long id){
        Observation observation = observationService.getObservationById(id);

        String path;
        for (var photo: observation.getObservationPhotos()){
            path = "Observations\\" + photo.getPathPhoto();
            InteractionPhoto.deletePhoto(path);
        }

        observationService.deleteObservationById(id);
        return "redirect:" + referrer;
    }

    @GetMapping("/students_observations")
    public String getStudentsObservations(Model model, @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "Все") String filter,
                         @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
                         @RequestParam(defaultValue = "object") String sortField,
                         @RequestParam(defaultValue = "asc") String sortDir, Principal principal,
                         @RequestParam(defaultValue = "card") String scheme) {
        try {
            if (keyword != null) {
                model.addAttribute("keyword", keyword);
            }
            if (filter != null) {
                model.addAttribute("filter", filter);
            }

            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("user", user);

            Page<Observation> pageObservations = observationService.listStudentsObservations(page, size, filter, keyword, sortField, sortDir, user.getId());
            List<Observation> observations = pageObservations.getContent();

            ArrayList<Long> list = new ArrayList<>();
            ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(0, 2, 4, 6, 8, 10));

            for (var observation : observations){
                for (var photo : observation.getObservationPhotos()){
                    try {
                        photo.setPathPhoto(InteractionPhoto.getPhoto(OBSERVATION_UPLOAD_DIRECTORY + photo.getPathPhoto()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            List<String> categories = new ArrayList<>();
            categories.add("Все");
            categoryService.getAllCategories().forEach(category -> {
                categories.add(category.getTitle());
            });

            model.addAttribute("observations", observations);
            model.addAttribute("categories", categories);
            model.addAttribute("activePage", "observations");

            model.addAttribute("arr", arr);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", pageObservations.getTotalElements());
            model.addAttribute("totalPages", pageObservations.getTotalPages());
            model.addAttribute("pageSize", size);

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("scheme", scheme);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "observations/observations";
    }
}
