package pl.auctane.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.auctane.order.dtos.order.OrderDto;
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

    public List<Order> getOrdersSorted() {
        return orderRepository.findAllByOrderByIdAsc();
    }

    public List<Order> getFinalizedOrders() {
        return orderRepository.findAllByFinalizedTrue();
    }

    public List<Order> getNotFinalizedOrders() {
        return orderRepository.findAllByFinalizedFalse();
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findAllByEmail(email);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order createOrder(OrderDto orderDto) {
        Order order = new Order(orderDto);
        return orderRepository.save(order);
    }

    public void deleteOrder(Order order) {
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
