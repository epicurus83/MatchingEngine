package creditsuisse.interview.alex.order;

import java.math.BigDecimal;

//Making non-final so ObjectPool can reuse same objects for orders and just overwrite the values
public class Order {

    public static final String MKT = "MKT";

    private String orderId;
    private String symbol;
    //Using BigDecimal for accuracy
    private BigDecimal price;
    private Side side;
    private OrderType orderType;
    //Assume Order Quantity is a whole number
    private int orderQuantity;
    private int fillQuantity;
    private String csvRepresentation = "";

    public String getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public boolean isLimit() {
        return OrderType.LIMIT.equals(orderType);
    }

    public boolean isMarket() {
        return OrderType.MARKET.equals(orderType);
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public int getFillQuantity() {
        return fillQuantity;
    }

    public void setFillQuantity(int fillQuantity) {
        this.fillQuantity = fillQuantity;
    }

    public String getCsvRepresentation() {
        return csvRepresentation;
    }

    public void setCsvRepresentation(String csvRepresentation) {
        this.csvRepresentation = csvRepresentation;
    }
}

