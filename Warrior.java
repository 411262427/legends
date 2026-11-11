import java.util.EnumSet;

public class Warrior extends Hero {
    public Warrior(String name, int mp, int strength, int agility,
                   int dexterity, int gold, int experience) {
        super(name, mp, strength, agility, dexterity, gold, experience,
              EnumSet.of(Stat.STRENGTH, Stat.AGILITY));
    }
}
