package pl.auctane.mail.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealDto {
    private Long id;
    private Meal meal;

    @Getter
    @Setter
    public static class Meal {
        private Long id;
        private String name;
        private String description;
    }
}
