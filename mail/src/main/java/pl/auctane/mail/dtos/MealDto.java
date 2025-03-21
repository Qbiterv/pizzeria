package pl.auctane.mail.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MealDto {
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;
}
