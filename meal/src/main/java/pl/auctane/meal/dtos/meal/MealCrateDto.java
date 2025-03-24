package pl.auctane.meal.dtos.meal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealCrateDto {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "category id is mandatory")
    @Min(value = 1, message = "Category id must be greater than 0")
    private Long categoryId;
}
