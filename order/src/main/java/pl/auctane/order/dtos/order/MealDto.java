package pl.auctane.order.dtos.order;

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

    public MealDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
