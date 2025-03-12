package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.entities.Order;
import pl.auctane.order.repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order createOrder(String name, String surname, String email, String phone, String address) {
        Order order = new Order();
        order.setName(name);
        order.setSurname(surname);
        order.setEmail(email);
        order.setPhone(phone);
        order.setAddress(address);

        return orderRepository.save(order);
    }

    public void removeOrder(Order order ) {
        orderRepository.delete(order);
    }

    public void setFinalized(Order order) {
        order.setFinalized(true);
        orderRepository.save(order);
    }

    public void setFinalized(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(value -> {
            value.setFinalized(true);
            orderRepository.save(value);
        });
    }
}
