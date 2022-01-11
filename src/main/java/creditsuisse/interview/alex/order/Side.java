package creditsuisse.interview.alex.order;

public enum Side {
    BUY("Buy"),
    SELL("Sell");

    private final String value;

    Side(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static Side getEnum(String value) {
        for(Side v : values())
            if(v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
