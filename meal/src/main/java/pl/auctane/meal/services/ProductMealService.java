package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.auctane.meal.dtos.productMeal.MealToSendDto;
import pl.auctane.meal.entities.Meal;
import pl.auctane.meal.entities.Product;
import pl.auctane.meal.entities.ProductMeal;
import pl.auctane.meal.repositories.ProductMealRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductMealService {
    private final ProductMealRepository productMealRepository;

    @Autowired
    public ProductMealService(ProductMealRepository productMealRepository) {
        this.productMealRepository = productMealRepository;
    }

    public List<Meal> getProductMeals(Long product) {
        return productMealRepository.findAllByProduct_Id(product, Sort.by(Sort.Direction.ASC,"meal.category")).stream().map(ProductMeal::getMeal).collect(Collectors.toList());
    }

    public void create(ProductMeal productMeal) {
        productMealRepository.save(productMeal);
    }
    public void create(Product product, Meal meal) {
        ProductMeal productMeal = new ProductMeal();
        productMeal.setProduct(product);
        productMeal.setMeal(meal);
        productMealRepository.save(productMeal);
    }

    public Optional<ProductMeal> getProductMeal(Long id) {
        return productMealRepository.findById(id);
    }

    public void delete(ProductMeal productMeal) {
        productMealRepository.delete(productMeal);
    }

    public List<ProductMeal> getAll() {
        return productMealRepository.findAll();
    }
}
