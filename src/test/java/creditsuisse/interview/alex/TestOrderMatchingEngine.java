package creditsuisse.interview.alex;

import creditsuisse.interview.alex.order.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Doesn't support Test Case E, in regard to no bookings being made for purely market orders. Didn't understand this as
in the test case, it looked like all the orders were Acknowledged and filled, even the MKT ones.
 */
public class TestOrderMatchingEngine {

    private static final ByteArrayOutputStream myOut = new ByteArrayOutputStream();

    @BeforeAll
    public static void stubStdOut() {
        System.setOut(new PrintStream(myOut));
    }

    @BeforeEach
    public void clearStdOutStub() {
        myOut.reset();
    }

    @Test
    public void testSampleA() throws IOException {
        List<Order> orders = DataParser.parseInput("""
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,20000
                Order2,0700.HK,610,Sell,10000
                Order3,0700.HK,610,Buy,10000
                """);
        OrderMatchingEngine orderMatchingEngine = new OrderMatchingEngine();
        orderMatchingEngine.matchOrders(orders);

        assertEquals("""
                        #ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity
                        Ack,Order1,0700.HK,610,Sell,20000
                        Ack,Order2,0700.HK,610,Sell,10000
                        Ack,Order3,0700.HK,610,Buy,10000
                        Fill,Order1,0700.HK,610,Sell,20000,610,10000
                        Fill,Order3,0700.HK,610,Buy,10000,610,10000
                        """.replace("\n", "\r\n"),
                myOut.toString());
    }

    @Test
    public void testSampleB() throws IOException {
        List<Order> orders = DataParser.parseInput("""
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,20000
                Order2,0700.HK,MKT,Sell,10000
                Order3,0700.HK,610,Buy,10000
                """);
        OrderMatchingEngine orderMatchingEngine = new OrderMatchingEngine();
        orderMatchingEngine.matchOrders(orders);

        assertEquals("""
                        #ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity
                        Ack,Order1,0700.HK,610,Sell,20000
                        Ack,Order2,0700.HK,MKT,Sell,10000
                        Ack,Order3,0700.HK,610,Buy,10000
                        Fill,Order2,0700.HK,MKT,Sell,10000,610,10000
                        Fill,Order3,0700.HK,610,Buy,10000,610,10000
                        """.replace("\n", "\r\n"),
                myOut.toString());
    }

    @Test
    public void testSampleC() throws IOException {
        List<Order> orders = DataParser.parseInput("""
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,10000
                Order2,0700.HK,610,Buy,10000000
                """);
        OrderMatchingEngine orderMatchingEngine = new OrderMatchingEngine();
        orderMatchingEngine.matchOrders(orders);

        assertEquals("""
                        #ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity
                        Ack,Order1,0700.HK,610,Sell,10000
                        Reject,Order2,0700.HK,610,Buy,10000000
                        """.replace("\n", "\r\n"),
                myOut.toString());
    }

    @Test
    public void testSampleD() throws IOException {
        List<Order> orders = DataParser.parseInput("""
                #OrderID,Symbol,Price,Side,OrderQuantity
                Order1,0700.HK,610,Sell,10000
                Order2,0005.HK,49.8,Sell,10000
                Order3,0005.HK,49.8,Buy,10000
                """);
        OrderMatchingEngine orderMatchingEngine = new OrderMatchingEngine();
        orderMatchingEngine.matchOrders(orders);

        assertEquals("""
                        #ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity
                        Ack,Order1,0700.HK,610,Sell,10000
                        Ack,Order2,0005.HK,49.8,Sell,10000
                        Ack,Order3,0005.HK,49.8,Buy,10000
                        Fill,Order2,0005.HK,49.8,Sell,10000,49.8,10000
                        Fill,Order3,0005.HK,49.8,Buy,10000,49.8,10000
                        """.replace("\n", "\r\n"),
                myOut.toString());
    }
}
