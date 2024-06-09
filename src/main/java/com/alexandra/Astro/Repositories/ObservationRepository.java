package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.Observation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    Page<Observation> findByObjectContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Observation> findByCategoryTitleContainingIgnoreCase(String filter, Pageable pageable);
    Page<Observation> findByObjectContainingIgnoreCaseAndCategoryTitleContainingIgnoreCase(String keyword, String filter, Pageable pageable);

    Page<Observation> findByUserTeacherIdLikeAndIsHomeworkTrue(Long id, Pageable pageable);
    Page<Observation> findByObjectContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(String keyword, Long id, Pageable pageable);
    Page<Observation> findByCategoryTitleContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(String filter, Long id, Pageable pageable);
    Page<Observation> findByObjectContainingIgnoreCaseAndCategoryTitleContainingIgnoreCaseAndUserTeacherIdLikeAndIsHomeworkTrue(String keyword, String filter, Long id, Pageable pageable);
}
