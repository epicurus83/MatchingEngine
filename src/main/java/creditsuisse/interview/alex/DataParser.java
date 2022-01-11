package creditsuisse.interview.alex;

import creditsuisse.interview.alex.order.Order;
import creditsuisse.interview.alex.order.OrderPool;
import creditsuisse.interview.alex.order.OrderType;
import creditsuisse.interview.alex.order.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

class DataParser {
    private static final OrderPool orderPool = OrderPool.getInstance();
    private static final List<Order> parsedOrders = new LinkedList<>();

    public static List<Order> parseInput(String stdIn) throws IOException {
        parsedOrders.clear();
        BufferedReader bufferedReader = new BufferedReader(new StringReader(stdIn));
        //Ignore the first line as it is the header (e.g. #OrderID,Symbol,Price,Side,OrderQuantity)
        bufferedReader.readLine();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splitLine = line.split(",");
            Order order = getOrder(splitLine);
            order.setCsvRepresentation(line);
            parsedOrders.add(order);
        }
        return parsedOrders;
    }

    /*
    Assuming that the format is always as expected (same as in example test cases). Sometimes STDIN might be incorrectly
    formatter, I would handle this by throwing an exception if format not what is expected and logging error message.
    I have skipped implementing this as I want to focus on the matching engine logic.
     */
    private static Order getOrder(String[] splitLine) {
        Order order = orderPool.getOrder();
        order.setOrderId(splitLine[0]);
        order.setSymbol(splitLine[1]);
        String price = splitLine[2];
        if (Order.MKT.equals(price)) {
            order.setOrderType(OrderType.MARKET);
        } else {
            order.setPrice(new BigDecimal(price));
            order.setOrderType(OrderType.LIMIT);

        }
        order.setSide(Side.getEnum(splitLine[3]));
        order.setOrderQuantity(Integer.parseInt(splitLine[4]));
        //probably not needed as int defaults to 0, but I think it's better to state explicitly.
        order.setFillQuantity(0);
        return order;
    }

    public static void clearAllInUseOrders() {
        orderPool.clearAllInUseOrders();
    }
}