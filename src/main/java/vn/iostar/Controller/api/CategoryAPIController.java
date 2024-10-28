package vn.iostar.Controller.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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


    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);

        if (category.isPresent()) {
            //return ResponseEntity.ok().body(category.get());
            return new ResponseEntity<Response>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            //return ResponseEntity.notFound().build();
            return new ResponseEntity<Response>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }



    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(@Validated @RequestParam("categoryName")
                                         String categoryName,
                                         @Validated @RequestParam("icon") MultipartFile icon) {
        Optional<Category> optCategory =
                categoryService.findByCategoryName(categoryName);

        if (optCategory.isPresent()) {
            return
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category đã tồn tại trong hệ thống");
                            //return new ResponseEntity<Response>(new Response(false, "Loại sản phẩm này đã tồn tại trong hệ thống", optCategory.get()), HttpStatus.BAD_REQUEST);
        } else {
            Category category = new Category();
            //kiểm tra tồn tại file, lưu file
            if(!icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                //lưu file vào trường Images
                category.setIcon(storageService.getSorageFilename(icon,
                        uuString));
                storageService.store(icon, category.getIcon());
            }

            category.setCategoryName(categoryName);

            categoryService.save(category);
            //return ResponseEntity.ok().body(category);
            return new ResponseEntity<Response>(new Response(true, "Thêm Thành công", category), HttpStatus.OK);
        }
    }
    @PutMapping(path = "/updateCategory")
    public ResponseEntity<?> updateCategory(@Validated @RequestParam("categoryId")
                                            Long categoryId,
                                            @Validated @RequestParam("categoryName") String categoryName,
                                            @Validated @RequestParam("icon") MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findById(categoryId);

        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }else if(optCategory.isPresent()) {

            //kiểm tra tồn tại file, lưu file
            if(!icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                //lưu file vào trường Images

                optCategory.get().setIcon(storageService.getSorageFilename(icon, uuString));
                storageService.store(icon,
                        optCategory.get().getIcon());
            }

            optCategory.get().setCategoryName(categoryName);
            categoryService.save(optCategory.get());
            //return ResponseEntity.ok().body(category);
            return new ResponseEntity<Response>(new
                    Response(true, "Cập nhật Thành công", optCategory.get()), HttpStatus.OK);
        }
        return null;
    }

    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId")
                                            Long categoryId){
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }else if(optCategory.isPresent()) {
            categoryService.delete(optCategory.get());
            //return ResponseEntity.ok().body(category);
            return new ResponseEntity<Response>(new Response(true, "Xóa Thành công", optCategory.get()), HttpStatus.OK);
        }

        return null;

    }
}