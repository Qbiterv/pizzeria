package pl.auctane.mail.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ProductDto {
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

