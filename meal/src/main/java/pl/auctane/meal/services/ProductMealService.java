package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductMealService {
    private final IngredientService ingredientService;

    @Autowired
    public ProductMealService(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }
}
