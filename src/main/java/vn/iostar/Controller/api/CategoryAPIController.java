package vn.iostar.Controller.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.entity.Category;
import vn.iostar.model.Response;
import vn.iostar.service.ICategoryService;
import vn.iostar.service.IStorageService;
@Controller
@RequestMapping(path = "/api/category")
public class CategoryAPIController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    IStorageService storageService;

    @GetMapping
    public String getAllCategory(Model model) {
        List<Category> list = categoryService.findAll();
        model.addAttribute("list",list);
        return "admin/category/list";
    }
    @GetMapping("/add")
    public String add(Category category, ModelMap model){
        model.addAttribute("category", category);
        return "admin/category/add";
    }

    @PostMapping("/saveadd")
    public String saveadd(Category category, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "admin/category/add";
        }
        categoryService.save(category);
        return "redirect:/api/category";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") long id, Model model){
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("category", category);
        return "admin/category/edit";
    }

    @PostMapping("/saveedit/{id}")
    public String update(@PathVariable("id") long id,Category category, BindingResult result,
                         Model model){
        if (result.hasErrors()){
            return "admin/category/edit";
        }
        category.setCategoryId(id);
        categoryService.save(category);
        return "redirect:/api/category";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, Model model){
        categoryService.deleteById(id);
        return "redirect:/api/category";
    }
}