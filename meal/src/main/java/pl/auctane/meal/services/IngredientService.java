package pl.auctane.meal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.meal.entities.Ingredient;
import pl.auctane.meal.repositories.IngredientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> getIngredient(Long id) {
        return ingredientRepository.findById(id);
    }

    public void createIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);

        ingredientRepository.save(ingredient);
    }
    public void createIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    public void updateIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    public boolean deleteIngredient(Long id) {
        if(getIngredient(id).isEmpty()) return false;

        ingredientRepository.deleteById(id);
        return true;
    }
}
