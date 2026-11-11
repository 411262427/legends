//Represents a map tile, including its type, accessibility, and optional market.
public class Tile {
    public enum Type { COMMON, MARKET, BLOCKED }
    private final Type type;
    private final Market market;
    public Tile(Type type, Market market) {
        if ((type == Type.MARKET) != (market != null)) {
            throw new IllegalArgumentException("Only market tiles own a market.");
        }
        this.type = type;
        this.market = market;
    }
    public Type getType() { return type; }
    public Market getMarket() { return market; }
    public boolean isAccessible() { return type != Type.BLOCKED; }
    public String symbol() { return type == Type.BLOCKED ? "#" : type == Type.MARKET ? "M" : "."; }
}
