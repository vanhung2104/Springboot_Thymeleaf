package vn.iostar.Controller.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iostar.entity.Category;
import vn.iostar.service.ICategoryService;

@Controller
@RequestMapping(path = "/api/category")
public class CategoryAPIController {
    @Autowired
    ICategoryService categoryService;

    @GetMapping
    public String getAllCategory(Model model) {
        List<Category> list = categoryService.findAll();
        model.addAttribute("list", list);
        return "admin/category/list";
    }

    @GetMapping("/add")
    public String add(Category category, ModelMap model) {
        model.addAttribute("category", category);
        return "admin/category/add";
    }

    @PostMapping("/saveadd")
    public String saveadd(Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/category/add";
        }
        categoryService.save(category);
        return "redirect:/api/category";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        model.addAttribute("category", category);
        return "admin/category/edit";
    }

    @PostMapping("/saveedit/{id}")
    public String update(@PathVariable("id") long id, Category category, BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "admin/category/edit";
        }
        category.setCategoryId(id);
        categoryService.save(category);
        return "redirect:/api/category";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, Model model) {
        categoryService.deleteById(id);
        return "redirect:/api/category";
    }

    @RequestMapping("/search-page")
    public String search(ModelMap model,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size) {
        int count = (int)categoryService.count();
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("categoryName"));
        Page<Category> resultPage = null;
        if (StringUtils.hasText(name)) {
            resultPage = categoryService.findByCategoryNameContaining(name, pageable);

            model.addAttribute("name", name);
        } else {
            resultPage = categoryService.findAll(pageable);
        }
        int totalPages = resultPage.getTotalPages();

        if (totalPages > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);

            if (totalPages > count) {
                if (end == totalPages) start = end - count;
                else if (start == 1) end = start + count;
            }

            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("categoryPage", resultPage);

        return "admin/category/list_page";
    }
}