package pl.auctane.meal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.auctane.meal.dtos.ProductCategory.ProductWithCategories;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.services.ProductCategoryService;
import pl.auctane.meal.services.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//passed sefel check

@RestController
@RequestMapping("/v1/product")
public class ProductController {
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductController(ProductService productService, ProductCategoryService productCategoryService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProducts() {
        List<Product> products = productService.getProducts();
        if (products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(productService.getProducts());
    }

    @GetMapping(value = "/get-with-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsAndCategories() {
        List<ProductWithCategories> productsAndCategories = productService.getProducts().stream().map(product -> new ProductWithCategories(product, productCategoryService.getCategoriesFromProductId(product.getId()))).collect(Collectors.toList());
        return ResponseEntity.ok().body(productsAndCategories);
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProduct(id);
        if(product.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(product.get());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        ObjectNode JSON = objectMapper.createObjectNode();

        if(product == null) {
            JSON.put("success", false);
            JSON.put("message", "Body is null");
            return ResponseEntity.badRequest().build();
        }
        if(product.getName() == null || product.getName().isEmpty()) {
            JSON.put("success", false);
            JSON.put("message", "Name is mandatory");
            return ResponseEntity.badRequest().body(JSON);
        }
        if(product.getPrice() < 0) {
            JSON.put("success", false);
            JSON.put("message", "Price must be greater than 0");
            return ResponseEntity.badRequest().body(JSON);
        }
        if(product.getDescription() == null) product.setDescription("");
        if(product.getImageUrl() == null) product.setImageUrl("");

        Product newProduct = new Product(product.getName(), product.getDescription(), product.getPrice(), product.getImageUrl());
        productService.createProduct(newProduct);

        JSON.put("success", true);
        JSON.put("message", "Created product: " + product.getName());
        return ResponseEntity.ok().body(JSON);
    }

    @PatchMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        ObjectNode JSON = objectMapper.createObjectNode();

        Optional<Product> productOptional = productService.getProduct(id);
        if(productOptional.isEmpty()) return ResponseEntity.badRequest().build();

        if(product.getName() != null && !product.getName().isEmpty()) productOptional.get().setName(product.getName());
        if(product.getDescription() != null) productOptional.get().setDescription(product.getDescription());
        if(product.getPrice() > 0 && !product.getPrice().equals(productOptional.get().getPrice())) productOptional.get().setPrice(product.getPrice());
        if(product.getImageUrl() != null) productOptional.get().setImageUrl(product.getImageUrl());

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
            JSON.put("message", "Product with id: " + id + " does not exist");
            return ResponseEntity.badRequest().body(JSON);
        }

        productService.deleteProduct(product.get());

        JSON.put("success", true);
        JSON.put("message", "Deleted product with id: " + id);
        return ResponseEntity.ok().body(JSON);
    }
}
