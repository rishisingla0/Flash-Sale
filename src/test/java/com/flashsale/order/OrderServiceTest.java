
package com.flashsale.order;

import com.flashsale.product.Product;
import com.flashsale.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStock(5);
    }

    @Test
    void createOrderShouldSaveOrderWhenStockIsAvailable() {
        OrderRequest request = new OrderRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(productRepository.deductStock(10L, 2))
                .thenReturn(1);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(request);

        assertNotNull(order);
        assertEquals(2, order.getQuantity());
        assertEquals(
                new BigDecimal("2000.00"),
                order.getTotalPrice()
        );
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        verify(productRepository).deductStock(10L, 2);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderShouldRejectWhenStockDeductionFails() {
        OrderRequest request = new OrderRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(productRepository.deductStock(10L, 2))
                .thenReturn(0);

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );

        verify(productRepository).deductStock(10L, 2);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
