package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.ObservationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ObservationPhotoRepository extends JpaRepository<ObservationPhoto, Long> {

}
