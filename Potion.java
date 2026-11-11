import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Potion extends Item {
    private final int amount;
    private final EnumSet<Stat> effects;
    public Potion(String name, int price, int level, int amount, Set<Stat> effects) {
        super(name, price, level, 1);
        this.amount = amount;
        this.effects = EnumSet.copyOf(effects);
    }
    public void applyTo(Hero hero) {
        for (Stat stat : effects) { hero.increaseStat(stat, amount); }
    }
    public Set<Stat> getEffects() { return Collections.unmodifiableSet(effects); }
    @Override public String describe() {
        return super.describe() + " | +" + amount + " " + effects;
    }
    @Override public Potion copy() {
        return new Potion(getName(), getPrice(), getRequiredLevel(), amount, effects);
    }
}
