package creditsuisse.interview.alex;

import creditsuisse.interview.alex.order.Order;
import creditsuisse.interview.alex.order.Side;

import java.math.BigDecimal;
import java.util.*;

public class OrderMatchingEngine {
    // Would make configurable if had time
    public static final int ORDER_CAPACITY = 50;

    private final Map<String, BuySellQueues> buySellQueuesMap = new HashMap<>();

    public void matchOrders(List<Order> orders) {
        System.out.println("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity");
        queueOrdersForMatching(orders);
        fillOrders();
    }

    private void queueOrdersForMatching(List<Order> orders) {
        for (Order order : orders) {
            if (order.getOrderQuantity() >= 10_000_000) {
                rejectOrder(order);
                continue;
            }
            BuySellQueues buySellQueues = getBuySellQueues(order);
            if (Side.BUY.equals(order.getSide())) {
                ackOrder(buySellQueues.getBuyQueue(), order);
                continue;
            }
            ackOrder(buySellQueues.getSellQueue(), order);
        }
    }

    private void rejectOrder(Order order) {
        System.out.println("Reject," + order.getCsvRepresentation());
    }

    /*
    This is a bad design, because during the trading day everytime a stock is traded for first time a new object will
    have to be instantiated. This has the drawback of being an expensive operation as well as resulting in the heap
    growing throughout the trading day. In actual implementation I would instantiate for all traded Symbols during startup.
    This means new objects not created during trading day as well as the total heap size being more consistent throughout the day.
     */
    private BuySellQueues getBuySellQueues(Order order) {
        String symbol = order.getSymbol();
        BuySellQueues buySellQueues = buySellQueuesMap.get(symbol);
        if (buySellQueues == null) {
            buySellQueues = new BuySellQueues();
            buySellQueuesMap.put(symbol, buySellQueues);
        }
        return buySellQueues;
    }

    private void ackOrder(PriorityQueue<Order> queue, Order order) {
        queue.offer(order);
        System.out.println("Ack," + order.getCsvRepresentation());
    }

    private void fillOrders() {
        for (BuySellQueues buySellQueues : buySellQueuesMap.values()) {
            PriorityQueue<Order> buyQueue = buySellQueues.getBuyQueue();
            PriorityQueue<Order> sellQueue = buySellQueues.getSellQueue();
            while (buyQueue.peek() != null && sellQueue.peek() != null) {
                Order currentBuyOrder = buyQueue.peek();
                Order currentSellOrder = sellQueue.peek();
                int buyUnfilledQuantity = currentBuyOrder.getOrderQuantity() - currentBuyOrder.getFillQuantity();
                int sellUnfilledQuantity = currentSellOrder.getOrderQuantity() - currentBuyOrder.getFillQuantity();
                int fillQuantity = Math.min(sellUnfilledQuantity, buyUnfilledQuantity);
                BigDecimal buyPrice = currentBuyOrder.getPrice();
                BigDecimal sellPrice = currentSellOrder.getPrice();
                BigDecimal fillPrice = (buyPrice == null) ? sellPrice : buyPrice;
                fillOrder(currentSellOrder, fillQuantity, fillPrice);
                fillOrder(currentBuyOrder, fillQuantity, fillPrice);
                pollOrders(buyQueue, sellQueue);
            }
        }
    }

    @SafeVarargs
    private void pollOrders(PriorityQueue<Order> ... orderQueues) {
        for (PriorityQueue<Order> orderQueue : orderQueues) {
            Order currentOrder = orderQueue.peek();
            if (currentOrder == null) {
                break;
            }
            if (currentOrder.getOrderQuantity() == currentOrder.getFillQuantity()) {
                orderQueue.poll();
            }
        }
    }

    private void fillOrder(Order order, int tradedQuantity, BigDecimal fillPrice) {
        order.setFillQuantity(order.getFillQuantity() + tradedQuantity);
        System.out.println("Fill," + order.getCsvRepresentation() + "," + fillPrice + "," + tradedQuantity);
    }
}