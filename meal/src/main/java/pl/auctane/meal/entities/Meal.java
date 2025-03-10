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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    public Meal() {
    }

    public Meal(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
