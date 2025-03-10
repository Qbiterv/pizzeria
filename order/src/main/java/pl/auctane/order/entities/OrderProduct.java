package pl.auctane.order.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.auctane.meal.entities.Product;

@Getter
@Setter
@Entity
@Table
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
