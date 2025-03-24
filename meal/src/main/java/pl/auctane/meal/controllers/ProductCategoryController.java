package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.ProductCategory.ProductCategoryCreateDto;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductCategory;
import pl.auctane.meal.services.CategoryService;
import pl.auctane.meal.services.ProductCategoryService;
import pl.auctane.meal.services.ProductService;

import java.util.List;
import java.util.Optional;

//passed sefel check

@RestController
@RequestMapping("v1/product-category")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService, ProductService productService, CategoryService categoryService, ObjectMapper objectMapper) {
        this.productCategoryService = productCategoryService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductCategories() {
        List<ProductCategory> productCategories = productCategoryService.findAll();
        if(productCategories.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(productCategories);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductCategoryById(@PathVariable Long id) {
        Optional<ProductCategory> productCategory = productCategoryService.findById(id);
        if(productCategory.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(productCategory);
    }

    @GetMapping(value = "/products-from-category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsFromCategoryId(@PathVariable Long id) {
        List<Product> products = productCategoryService.getProductsFromCategoryId(id);
        if(products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(products);
    }

    @GetMapping(value = "/categories-from-product/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCategoriesFromProductId(@PathVariable Long id) {
        List<Category> categories = productCategoryService.getCategoriesFromProductId(id);
        if(categories.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(categories);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProductCategory(@RequestBody ProductCategoryCreateDto productCategory) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> product = productService.getProduct(productCategory.getProductId());
        if(product.isEmpty()){
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + productCategory.getProductId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        Optional<Category> category = categoryService.getCategory(productCategory.getCategoryId());
        if(category.isEmpty()){
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + productCategory.getCategoryId() + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        ProductCategory newProductCategory = new ProductCategory(product.get(), category.get());

        productCategoryService.save(newProductCategory);

        JSON.put("success", true);
        JSON.put("message", "Connected " + product.get().getName() + " with " + category.get().getName());
        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProductCategory(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<ProductCategory> productCategory = productCategoryService.findById(id);

        if(productCategory.isEmpty()){
            JSON.put("success", false);
            JSON.put("message", "Product-category with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        productCategoryService.deleteById(id);

        JSON.put("success", true);
        JSON.put("message", "Deleted product category: " + id);
        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductCategory(@PathVariable("id") Long id, @RequestBody ProductCategoryCreateDto productCategory) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<ProductCategory> productCategoryToUpdate = productCategoryService.findById(id);

        if (productCategoryToUpdate.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product category with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        //change values
        if(productCategory.getProductId() != null && productCategory.getProductId() > 0) {
            Optional<Product> product = productService.getProduct(productCategory.getProductId());
            if (product.isEmpty()) {
                JSON.put("success", false);
                JSON.put("message", "Product with id: " + productCategory.getProductId() + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            productCategoryToUpdate.get().setProduct(product.get());
        }

        if(productCategory.getCategoryId() != null && productCategory.getCategoryId() > 0) {
            Optional<Category> category = categoryService.getCategory(productCategory.getCategoryId());
            if (category.isEmpty()) {
                JSON.put("success", false);
                JSON.put("message", "Category with id: " + productCategory.getCategoryId() + " does not exist");
                return ResponseEntity.badRequest().body(JSON);
            }
            productCategoryToUpdate.get().setCategory(category.get());
        }

        productCategoryService.save(productCategoryToUpdate.get());

        JSON.put("success", true);
        JSON.put("message", "Updated product category");
        return ResponseEntity.ok().body(JSON);
    }
}
