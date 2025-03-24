package pl.auctane.order.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.auctane.order.dtos.order.OrderDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column()
    private boolean finalized = false;

    public Order() {}
    public Order(OrderDto orderDto) {
        this.name = orderDto.getName();
        this.surname = orderDto.getSurname();
        this.email = orderDto.getEmail();
        this.phone = orderDto.getPhone();
        this.address = orderDto.getAddress();
    }
}
