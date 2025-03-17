package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.category.CategoryDto;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.services.CategoryService;

import java.util.Optional;

@RestController
@RequestMapping("/v1/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok().body(categoryService.getCategories());
    }

    @GetMapping("/get/name={name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(categoryService.getCategories(name));
    }

    @GetMapping("/get/id={id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(categoryService.getCategory(id));
    }


    //todo parse PLAIN TEXT from json to string value
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(@RequestBody String name) {
        Category newCategory = new Category();
        newCategory.setName(name);
        categoryService.createCategory(newCategory);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
        Optional<Category> category = categoryService.getCategory(id);

        ObjectNode JSON = objectMapper.createObjectNode();

        if(category.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        categoryService.deleteCategory(id);

        JSON.put("success", true);
        JSON.put("message", "Deleted category with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editCategory(@PathVariable("id") Long id, @RequestBody CategoryDto categoryDto) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Category> category = categoryService.getCategory(id);

        if(category.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + id + " doesn't exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(categoryDto == null) {
            JSON.put("success", false);
            JSON.put("message", "No body");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Invalid category name");
            return ResponseEntity.badRequest().body(JSON);
        }

        if(categoryDto.getName().equals(category.get().getName())) {
            JSON.put("success", false);
            JSON.put("message", "Category with id " + id + " already has a name" + categoryDto.getName());
            return ResponseEntity.badRequest().body(JSON);
        }

        category.get().setName(categoryDto.getName());
        categoryService.updateCategory(category.get());

        JSON.put("success", true);
        JSON.put("message", "Updated category with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }
}
