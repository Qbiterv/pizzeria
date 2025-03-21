package pl.auctane.meal.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private MealCategory category;

    public Meal() {}
    public Meal(String name, String description, MealCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }
}
