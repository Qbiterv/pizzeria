package pl.auctane.mail.dtos.product;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductDto {
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;

    private double price;

    private Category category;

    @Getter
    @Setter
    public static class Category {
        private Long id;
        private String name;
    }
}

