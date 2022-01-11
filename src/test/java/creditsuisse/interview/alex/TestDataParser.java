package creditsuisse.interview.alex;

import creditsuisse.interview.alex.order.Order;
import creditsuisse.interview.alex.order.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestDataParser {

    @BeforeEach
    public void setup() {
        DataParser.clearAllInUseOrders();
    }
    @Test
    public void testCanParseValidLimitOrder() throws IOException {

        String input = """
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,20000
                """;

        List<Order> orders = DataParser.parseInput(input);

        assertEquals(1, orders.size());
        Order bookedOrder = orders.get(0);
        assertNotNull(bookedOrder);
        assertEquals("Order1", bookedOrder.getOrderId());
        assertEquals("0700.HK", bookedOrder.getSymbol());
        assertEquals(new BigDecimal("610"), bookedOrder.getPrice());
        assertTrue(bookedOrder.isLimit());
        assertEquals(Side.SELL, bookedOrder.getSide());
        assertEquals(20_000, bookedOrder.getOrderQuantity());
        assertEquals(0, bookedOrder.getFillQuantity());
        assertEquals("Order1,0700.HK,610,Sell,20000", bookedOrder.getCsvRepresentation());
    }

    @Test
    public void testCanParseValidMarketOrder() throws IOException {

        String input = """
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order2,0700.HK,MKT,Buy,20000
                """;

        List<Order> orders = DataParser.parseInput(input);

        assertEquals(1, orders.size());
        Order bookedOrder = orders.get(0);
        assertNotNull(bookedOrder);
        assertEquals("Order2", bookedOrder.getOrderId());
        assertEquals("0700.HK", bookedOrder.getSymbol());
        assertNull(bookedOrder.getPrice());
        assertTrue(bookedOrder.isMarket());
        assertEquals(Side.BUY, bookedOrder.getSide());
        assertEquals(20_000, bookedOrder.getOrderQuantity());
        assertEquals(0, bookedOrder.getFillQuantity());
        assertEquals("Order2,0700.HK,MKT,Buy,20000", bookedOrder.getCsvRepresentation());
    }

    @Test
    public void testCanBookMultipleOrders() throws IOException {

        String input = """
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,20000
                Order2,0700.HK,610,Sell,10000
                Order3,0700.HK,610,Buy,10000
                Order4,0500.HK,MKT,Buy,10000
                """;

        List<Order> orders = DataParser.parseInput(input);

        assertEquals(4, orders.size());
        for (int i = 0; i < 4; i++) {
            assertEquals("Order" + (i + 1), orders.get(i).getOrderId());
        }
    }
}
