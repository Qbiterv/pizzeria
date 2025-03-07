package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.dtos.productMeal.ProductMealsListDto;
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

    public List<ProductMealsListDto> getProductMeals(Product product) {
        return productMealRepository.getAllByProductId(product).stream()
                .map(productMeal -> new ProductMealsListDto(productMeal.getId(), new Meal(productMeal.getMeal().getId(), productMeal.getMeal().getName(), productMeal.getMeal().getDescription())))
                .collect(Collectors.toList());
    }

    public void save(ProductMeal productMeal) {
        productMealRepository.save(productMeal);
    }

    public Optional<ProductMeal> getProductMeal(int id) {
        return productMealRepository.findById(id);
    }

    public void delete(ProductMeal productMeal) {
        productMealRepository.delete(productMeal);
    }

    public List<ProductMeal> getAll() {
        return productMealRepository.findAll();
    }
}
