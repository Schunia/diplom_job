package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.ArticlePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, Long> {

}
