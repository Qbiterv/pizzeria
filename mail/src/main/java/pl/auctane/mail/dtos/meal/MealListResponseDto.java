package pl.auctane.mail.dtos.meal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MealListResponseDto {
    private List<MealDto> meals;
}
