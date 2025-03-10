package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.product.ProductCreateDto;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.services.CategoryService;
import pl.auctane.meal.services.ProductService;

import java.util.Optional;

@RestController
@RequestMapping("/v1/product")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductController(ProductService productService, ObjectMapper objectMapper, CategoryService categoryService) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok().body(productService.getProducts());
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProduct(id);

        if(product.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(product.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDto product) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(product == null) return ResponseEntity.badRequest().build();

        if(product.getName() == null || product.getName().isEmpty()) return ResponseEntity.badRequest().build();
        if(product.getPrice() < 0) return ResponseEntity.badRequest().build();
        if(product.getCategoryId() < 0) return ResponseEntity.badRequest().build();
        if(product.getDescription() == null) product.setDescription("");

        Optional<Category> category = categoryService.getCategory(product.getCategoryId());

        if(category.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Category with id: " + product.getCategoryId() + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setCategory(category.get());
        newProduct.setPrice(product.getPrice());
        newProduct.setDescription(product.getDescription());

        productService.createProduct(newProduct);

        JSON.put("success", true);
        JSON.put("message", "Created product: " + product.getName());

        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editProduct(@PathVariable("id") Long id, @RequestBody ProductCreateDto product) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> productOptional = productService.getProduct(id);
        if(productOptional.isEmpty()) return ResponseEntity.badRequest().build();

        if(product.getName() != null && !product.getName().isEmpty()) productOptional.get().setName(product.getName());
        if(product.getDescription() != null) productOptional.get().setDescription(product.getDescription());
        if(product.getPrice() > 0 && product.getPrice() != productOptional.get().getPrice()) productOptional.get().setPrice(product.getPrice());

        if(product.getCategoryId() > 0 && productOptional.get().getCategory().getId() != product.getCategoryId()) {
            Optional<Category> category = categoryService.getCategory(product.getCategoryId());
            if(category.isEmpty()) {
                JSON.put("success", false);
                JSON.put("message", "Category with id: " + product.getCategoryId() + " doesn't exist");

                return ResponseEntity.badRequest().body(JSON);
            }

            productOptional.get().setCategory(category.get());
        }

        productService.updateProduct(productOptional.get());

        JSON.put("success", true);
        JSON.set("product", objectMapper.valueToTree(productOptional.get()));

        return ResponseEntity.ok().body(JSON);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> product = productService.getProduct(id);

        if(product.isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Product with id: " + id + " doesn't exist");

            return ResponseEntity.badRequest().body(JSON);
        }

        productService.deleteProduct(product.get());

        JSON.put("success", true);
        JSON.put("message", "Deleted product with id: " + id);

        return ResponseEntity.ok().body(JSON);
    }
}
