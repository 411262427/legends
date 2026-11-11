//Represents a repairable weapon with damage and hand requirements.
public class Weapon extends Item {
    private final int damage;
    private final int requiredHands;

    public Weapon(String name, int price, int level, int damage, int requiredHands) {
        super(name, price, level, 20);
        this.damage = damage;
        this.requiredHands = requiredHands;
    }
    public int getDamage() { return damage; }
    public int getRequiredHands() { return requiredHands; }
    @Override public boolean canRepair() { return true; }
    @Override public String describe() {
        return super.describe() + " | damage " + damage + " | hands " + requiredHands;
    }
    @Override public Weapon copy() {
        return new Weapon(getName(), getPrice(), getRequiredLevel(), damage, requiredHands);
    }
}
