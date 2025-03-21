package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.ProductCategory;
import pl.auctane.meal.repositories.ProductCategoryRepository;

import java.util.List;

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
}
