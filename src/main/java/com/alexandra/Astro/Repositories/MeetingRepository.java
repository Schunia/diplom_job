package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.Meeting;
import com.alexandra.Astro.Models.Observation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByStatusNotContaining(String status);
    Page<Meeting> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Meeting> findByStatusContainingIgnoreCase(String filter, Pageable pageable);
    Page<Meeting> findByTitleContainingIgnoreCaseAndStatusContainingIgnoreCase(String keyword, String filter, Pageable pageable);
}
