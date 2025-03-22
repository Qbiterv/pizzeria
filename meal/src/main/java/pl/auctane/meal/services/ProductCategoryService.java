package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Category;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductCategory;
import pl.auctane.meal.repositories.ProductCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductCategory> findAll() {
        return productCategoryRepository.findAll();
    }

    public ProductCategory findById(Long id) {
        return productCategoryRepository.findById(id).orElse(null);
    }

    public void save(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }

    public void deleteById(Long id) {
        productCategoryRepository.deleteById(id);
    }

    public List<Product> getProductsFromCategoryId(Long id) {
        return productCategoryRepository.findAllByCategory_Id(id).stream().map(ProductCategory::getProduct).collect(Collectors.toList());
    }

    public List<Category> getCategoriesFromProductId(Long id) {
        return productCategoryRepository.findAllByProduct_Id(id).stream().map(ProductCategory::getCategory).collect(Collectors.toList());
    }
}
