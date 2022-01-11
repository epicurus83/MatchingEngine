package creditsuisse.interview.alex;

import creditsuisse.interview.alex.order.Order;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.PriorityQueue;

public class BuySellQueues {

    //Coding to implementation as PriorityQueue needed for Price/Time matching engine algorithm to work
    private final PriorityQueue<Order> buyQueue = new PriorityQueue<>(OrderMatchingEngine.ORDER_CAPACITY, priceTimeComparator);
    private final PriorityQueue<Order> sellQueue = new PriorityQueue<>(OrderMatchingEngine.ORDER_CAPACITY, priceTimeComparator);

    private static final Comparator<Order> priceTimeComparator = (order1, order2) -> {
        if (order1.isMarket() && order2.isMarket()) {
            return compareTime(order1, order2);
        }
        if (order1.isMarket()) {
            return -1;
        }
        if (order2.isMarket()) {
            return 1;
        }
        BigDecimal order1Price = order1.getPrice();
        BigDecimal order2Price = order2.getPrice();
        BigDecimal priceDelta = order2Price.subtract(order1Price);
        if (BigDecimal.ZERO.equals(priceDelta)) {
            return compareTime(order1, order2);
        }
        return priceDelta.intValue();
    };

    // As no Transaction Time passed in StdIn, will assume that the integer in Order ID can act as transact time
    private static int compareTime(Order order1, Order order2) {
        int order1Num = getOrderIdAsInt(order1);
        int order2Num = getOrderIdAsInt(order2);
        return order1Num - order2Num;
    }

    private static int getOrderIdAsInt(Order order1) {
        return Integer.parseInt(order1.getOrderId().substring(5));
    }

    public PriorityQueue<Order> getBuyQueue() {
        return buyQueue;
    }

    public PriorityQueue<Order> getSellQueue() {
        return sellQueue;
    }
}
