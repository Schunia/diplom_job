package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.ObservationPhoto;
import com.alexandra.Astro.Repositories.ObservationPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObservationPhotoService {
    private ObservationPhotoRepository observationPhotoRepository;

    @Autowired
    public void setObservationPhotoRepository(ObservationPhotoRepository observationPhotoRepository) {
        this.observationPhotoRepository = observationPhotoRepository;
    }

    public List<ObservationPhoto> getAllObservationPhotos() {
        return observationPhotoRepository.findAll();
    }

    public ObservationPhoto getObservationPhotoById(Long id) {
        return observationPhotoRepository.findById(id).get();
    }

    public void insertObservationPhoto(ObservationPhoto observationPhoto) {
        observationPhotoRepository.save(observationPhoto);
    }

    public void deleteObservationPhotoById(Long id) {
        observationPhotoRepository.deleteById(id);
    }
}
