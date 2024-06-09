package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Category;
import com.alexandra.Astro.Services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public void setServices(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    private void fillModelWithCategory(Model model, Category category){
        model.addAttribute("category", category);
    }

    @GetMapping("/categories")
    public String placesPage(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("activePage", "categories");
        return "categories/categories";
    }

    @GetMapping("/categories/create")
    public String createCategory(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                                 Model model) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        fillModelWithCategory(model, new Category());
        return "categories/insert";
    }

    @GetMapping("/categories/details/{id}")
    public String detailsPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                              Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Category selectedCategory = categoryService.getCategoryById(id).get();

        model.addAttribute("selectedCategory", selectedCategory);

        return "categories/details";
    }

    @GetMapping("/categories/edit/{id}")
    public String editPage(@RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer,
                           Model model, @PathVariable("id") Long id) {

        if (referrer != null) {
            model.addAttribute("previousUrl", referrer);
        }

        Category selectedCategory = categoryService.getCategoryById(id).get();

        model.addAttribute("selectedCategory", selectedCategory);

        return "categories/edit";
    }

    @PostMapping("/categories/addingCategory")
    public String insertCategory(Category category){
        categoryService.insertCategory(category);
        return "redirect:/categories";
    }

    @PostMapping("/categories/editCategory")
    public String editCategory(Category category){
        categoryService.insertCategory(category);
        return "redirect:/categories";
    }

//    @GetMapping("/categories/editCategory/{id}/{title}")
//    public String editCategory(@PathVariable("id") Long id, @PathVariable("title") String title){
//        Category category = categoryService.getCategoryById(id).get();
//        categoryService.insertCategory(category);
//        return "redirect:/categories";
//    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategoryById(@PathVariable("id") Long id){
        categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }
}
