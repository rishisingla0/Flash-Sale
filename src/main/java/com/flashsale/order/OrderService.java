
package com.flashsale.order;

import com.flashsale.product.Product;
import com.flashsale.product.ProductNotFoundException;
import com.flashsale.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {

        // Find the product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Calculate total price using the current product price
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        //Atomically deduct stock
        int updatedRows = productRepository.deductStock(request.getProductId(), request.getQuantity());
        if(updatedRows == 0) {
            throw new InsufficientStockException("Insufficient stock");
        }
        
        // Create order
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());

        // Save order
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

}
