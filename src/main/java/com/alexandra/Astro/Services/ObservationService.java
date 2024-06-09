package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.Observation;
import com.alexandra.Astro.Repositories.ObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObservationService {
    private ObservationRepository observationRepository;

    @Autowired
    public void setObservationRepository(ObservationRepository observationRepository) {
        this.observationRepository = observationRepository;
    }

    public List<Observation> getAllObservations() {
        return observationRepository.findAll();
    }

    public Observation getObservationById(Long id) {
        return observationRepository.findById(id).get();
    }
    public void insertObservation(Observation observation) {
        observationRepository.save(observation);
    }

    public void deleteObservationById(Long id) {
        observationRepository.deleteById(id);
    }

    public List<File> getPhotos(Observation observation) {
        var observationPhotos = observation.getObservationPhotos();
        if (observationPhotos.size() == 0){
            return new ArrayList<>();
        }
        else{
            String path = observationPhotos.get(0).getPathPhoto();
            File photo = new File(path);
            List<File> photos = new ArrayList<>();
            photos.add(photo);
            return photos;
        }
    }

    /*
     * TODO: Get Observation By keyword and Pagination
     */
    public Page<Observation> listAll(int pageNum, int size, String filter, String keyword, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Observation> pageObservations;
        if (keyword == null && filter.equals("Все")) {
            pageObservations = observationRepository.findAll(pageable);
        } else if(keyword != null && filter.equals("Все")){
            pageObservations = observationRepository.findByObjectContainingIgnoreCase(keyword, pageable);
        } else if(keyword == null){
            pageObservations = observationRepository.findByCategoryTitleContainingIgnoreCase(filter, pageable);
        } else
            pageObservations = observationRepository.findByObjectContainingIgnoreCaseAndCategoryTitleContainingIgnoreCase(keyword, filter, pageable);

        return pageObservations;
    }

    public Page<Observation> listStudentsObservations(int pageNum, int size, String filter, String keyword,
                                                      String sortField, String sortDir, Long teacherId) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Observation> pageObservations;
        if (keyword == null && filter.equals("Все")) {
            pageObservations = observationRepository.findByUserTeacherIdLikeAndIsHomeworkTrue(teacherId, pageable);
        } else if(keyword != null && filter.equals("Все")){
            pageObservations = observationRepository.findByObjectContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(keyword, teacherId, pageable);
        } else if(keyword == null){
            pageObservations = observationRepository.findByCategoryTitleContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(filter, teacherId, pageable);
        } else
            pageObservations = observationRepository.findByObjectContainingIgnoreCaseAndCategoryTitleContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(keyword, filter, teacherId, pageable);

        return pageObservations;
    }
}
