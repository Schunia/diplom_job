package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.Article;
import com.alexandra.Astro.Repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private ArticleRepository articleRepository;

    @Autowired
    public void setRepository(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).get();
    }
//    public Article getGuideByEMail(String email) {
//        return articleRepository.findByEmail(email).get();
//    }

    public void insertArticle(Article article) {
        articleRepository.save(article);
    }

    public void deleteArticleById(Long id) {
        articleRepository.deleteById(id);
    }

    /*
     * TODO: Get Mark By keyword and Pagination
     */
    public Page<Article> listAll(int pageNum, int size, String keyword, String sortField, String sortDir, Boolean isLecture) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Article> pageArticles;
        if (isLecture) {
            if (keyword == null) {
                pageArticles = articleRepository.findByIsLectureTrue(pageable);
            } else {
                pageArticles = articleRepository.findByTitleContainingIgnoreCaseAndIsLectureTrue(keyword, pageable);
            }
        } else {
            if (keyword == null) {
                pageArticles = articleRepository.findByIsLectureFalse(pageable);
            } else {
                pageArticles = articleRepository.findByTitleContainingIgnoreCaseAndIsLectureFalse(keyword, pageable);
            }
        }

        return pageArticles;
    }
}
