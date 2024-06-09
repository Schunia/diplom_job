package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Article> findByIsLectureFalse(Pageable pageable);
    Page<Article> findByIsLectureTrue(Pageable pageable);
    Page<Article> findByTitleContainingIgnoreCaseAndIsLectureFalse(String keyword, Pageable pageable);
    Page<Article> findByTitleContainingIgnoreCaseAndIsLectureTrue(String keyword, Pageable pageable);
}
