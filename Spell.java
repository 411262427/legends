//Defines shared spell attributes, calculates damage, and requires subclasses to define weakening effects.
public abstract class Spell extends Item {
    private final int damage;
    private final int manaCost;
    protected Spell(String name, int price, int level, int damage, int manaCost) {
        super(name, price, level, 3);
        this.damage = damage;
        this.manaCost = manaCost;
    }
    public int getBaseDamage() { return damage; }
    public int getManaCost() { return manaCost; }
    public int damageFor(int dexterity) {
        return GameRules.rounded(damage * (1 + dexterity / 10000.0));
    }
    public abstract void weaken(Monster target);
    @Override public String describe() {
        return super.describe() + " | damage " + damage + " | mana " + manaCost;
    }
}
