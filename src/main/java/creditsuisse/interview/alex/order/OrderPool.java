package creditsuisse.interview.alex.order;

import creditsuisse.interview.alex.OrderMatchingEngine;

import java.util.LinkedList;
import java.util.Queue;

/*
Using Order Pool because if an Order object created for each order throughout the day then not needed after order
booked, this will lead to a lot of GC which is bad for performance. This way no Order objects should need to be
GCed.
 */
public class OrderPool {

    private static OrderPool instance;

    private final Queue<Order> availableOrders = new LinkedList<>();
    private final Queue<Order> inUseOrders = new LinkedList<>();


    private OrderPool() {
        for (int i = 0; i < OrderMatchingEngine.ORDER_CAPACITY; i++) {
            availableOrders.offer(new Order());
        }
    }

    public static OrderPool getInstance() {
        if (instance == null) {
            instance = new OrderPool();
        }
        return instance;
    }

    public Order getOrder() {
        Order order = availableOrders.poll();
        if (order == null) {
            order = new Order();
        }
        inUseOrders.offer(order);
        return order;
    }

    public void clearAllInUseOrders() {
        Order order;
        while ((order = inUseOrders.poll()) != null) {
            availableOrders.offer(order);
        }
    }
}
