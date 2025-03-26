package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.auctane.order.dtos.meal.MealDto;
import pl.auctane.order.dtos.meal.MealListResponseDto;
import pl.auctane.order.dtos.meal.MealWithQuantityDto;
import pl.auctane.order.dtos.product.ProductDto;
import pl.auctane.order.dtos.product.ProductIdWithQuantityDto;
import pl.auctane.order.dtos.product.ProductWithQuantityAndMealsDto;
import pl.auctane.order.dtos.product.ProductWithQuantityDto;

import java.util.*;

@Service
public class MealModuleService {

    @Value("${service.meal.url}")
    private String mealServiceUrl;

    public List<ProductWithQuantityAndMealsDto> getValidProductList(List<ProductIdWithQuantityDto> productIds) throws IllegalArgumentException{
        List<ProductWithQuantityAndMealsDto> productsWithQuantity = new ArrayList<>();

        //for validation
        List<Long> productsIdsList = new ArrayList<>();

        // Check if all products exist and add them to list
        for (ProductIdWithQuantityDto productIdWithQuantity : productIds) {

            if (productIdWithQuantity.getProductId() == null || productIdWithQuantity.getProductId() < 1 || productIdWithQuantity.getQuantity() < 1)
                throw new IllegalArgumentException("Product id or quantity is invalid");

            if (productsIdsList.contains(productIdWithQuantity.getProductId()))
                throw new IllegalArgumentException("List cannot have duplicated products");

            Optional<ProductDto> product = getProductFromId(productIdWithQuantity.getProductId());

            if (product.isEmpty())
                throw new IllegalArgumentException("Product with id " + productIdWithQuantity.getProductId() + " does not exist");


            List<MealWithQuantityDto> mealsWithQuantity = getMealsWithQuantity(productIdWithQuantity.getProductId());

            //add product to list
            productsWithQuantity.add(productIdWithQuantity.toProductWithQuantityAndMeals(product.get(), mealsWithQuantity));
            //add id to validation list
            productsIdsList.add(productIdWithQuantity.getProductId());
        }

        return productsWithQuantity;
    }
    public List<ProductWithQuantityAndMealsDto> getProductWithMealsList(List<ProductWithQuantityDto> productsWithQuantity) throws IllegalArgumentException{

        List<ProductWithQuantityAndMealsDto> productsWithMeals = new ArrayList<>();

        for (ProductWithQuantityDto productWithQuantity : productsWithQuantity) {
            try{
                //get meals for product and add new object to list
                productsWithMeals.add(productWithQuantity.toProductWithQuantityAndMeals(getMealsWithQuantity(productWithQuantity.getProduct().getId())));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error occur while receiving meal list for product. Message: " + e);
            }
        }

        return productsWithMeals;
    }

    private List<MealWithQuantityDto> getMealsWithQuantity(Long productId) throws IllegalArgumentException {
        String url = mealServiceUrl + "/product-meal/product/" + productId;

        ResponseEntity<MealListResponseDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, MealListResponseDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            throw new IllegalArgumentException("Error occur while receiving meal list for product. Message: " + e);
        }

        List<MealDto> meals;

        try {
            meals = Objects.requireNonNull(response.getBody()).getMeals();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Error occur while receiving meal list. The body of response is null");
        }

        //if there is only one meal, return empty list
        if (meals.size() <= 1)
            return new ArrayList<>();

        //list of meal and its index in the list
        HashMap<MealDto, Integer> mealsDictionary = new HashMap<>();
        //final list
        List<MealWithQuantityDto> mealsWithQuantity = new ArrayList<>();

        //connect meals
        for (MealDto meal : meals) {
            //if meal already exist in the list, increase quantity
            if(mealsDictionary.containsKey(meal))
                mealsWithQuantity.get(mealsDictionary.get(meal)).setQuantity(mealsWithQuantity.get(mealsDictionary.get(meal)).getQuantity() + 1);
                //else put it into list and dictionary of indexes
            else {
                mealsWithQuantity.add(new MealWithQuantityDto(meal, 1));
                mealsDictionary.put(meal, meals.indexOf(meal));
            }
        }

        return mealsWithQuantity;
    }
    public Optional<ProductDto> getProductFromId(Long product) {
        String url = mealServiceUrl + "/product/get/" + product;

        ResponseEntity<ProductDto> response = null;

        try {
            response = new RestTemplate().getForEntity(url, ProductDto.class);
        } catch (HttpStatusCodeException | ResourceAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
