package pl.auctane.meal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    private Double price;
}
