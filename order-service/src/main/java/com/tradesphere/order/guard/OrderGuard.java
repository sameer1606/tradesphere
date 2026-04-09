package com.tradesphere.order.guard;

import com.tradesphere.order.domain.Order;
import com.tradesphere.order.domain.OrderStatus;
import com.tradesphere.order.exception.OrderNotCancellableException;
import org.springframework.stereotype.Component;


@Component
public class OrderGuard {

    public void validateCancellableOrder(Order order) {

        if (order.getOrderStatus() == OrderStatus.CANCELLED ||
                order.getOrderStatus() == OrderStatus.EXECUTED ||
                order.getOrderStatus() == OrderStatus.REJECTED) {
            throw new OrderNotCancellableException(order.getOrderId(), order.getOrderStatus());
        }
    }
}
