package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.category.CategoryDto;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.services.CategoryService;

import java.util.List;
import java.util.Optional;

//Passed sefel check

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @GetMapping("/get")
    public ResponseEntity<?> getCategories() {
        List<Category> categories = categoryService.getCategories();
        if (categories.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/get/name={name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable("name") String name) {
        Optional<Category> category = categoryService.getCategoryByName(name);
        if (category.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(category.get());
    }

    @GetMapping("/get/id={id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Long id) {
        Optional<Category> category = categoryService.getCategory(id);
        if (category.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(category.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(@RequestBody CategoryDto category) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Category> categoryWithSameName = categoryService.getCategoryByName(category.getName());

        if(categoryWithSameName.isPresent()) {
            JSON.put("success", false);
            JSON.put("message", "Category with name: " + category.getName() + " already exists");
            return ResponseEntity.badRequest().body(JSON);
        }

        categoryService.createCategory(category.getName());

        JSON.put("success", true);
        JSON.put("message", "Created category: " + category.getName());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Category> category = categoryService.getCategory(id);

        if(category.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + id + " does not exist");
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
            JSON.put("message", "Name is mandatory");
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
