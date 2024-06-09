package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.News;
import com.alexandra.Astro.Repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private NewsRepository newsRepository;

    @Autowired
    public void setNewsRepository(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id).get();
    }

    public void insertNews(News news) {
        newsRepository.save(news);
    }

    public void deleteNewsById(Long id) {
        newsRepository.deleteById(id);
    }

    /*
     * TODO: Get News by Pagination
     */
    public Page<News> listAll(int pageNum, int size, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<News> pageNews = newsRepository.findAll(pageable);

        return pageNews;
    }
}
