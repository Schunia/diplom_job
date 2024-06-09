package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.ArticlePhoto;
import com.alexandra.Astro.Repositories.ArticlePhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticlePhotoService {
    private ArticlePhotoRepository articlePhotoRepository;

    @Autowired
    public void setExcursionPhotoRepository(ArticlePhotoRepository articlePhotoRepository) {
        this.articlePhotoRepository = articlePhotoRepository;
    }

    public List<ArticlePhoto> getAllArticlePhotos() {
        return articlePhotoRepository.findAll();
    }

    public ArticlePhoto getArticlePhotoById(Long id) {
        return articlePhotoRepository.findById(id).get();
    }

    public void insertArticlePhoto(ArticlePhoto articlePhoto) {
        articlePhotoRepository.save(articlePhoto);
    }

    public void deleteArticlePhotoById(Long id) {
        articlePhotoRepository.deleteById(id);
    }
}
