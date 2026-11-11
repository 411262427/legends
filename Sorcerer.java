import java.util.EnumSet;

public class Sorcerer extends Hero {
    public Sorcerer(String name, int mp, int strength, int agility,
                   int dexterity, int gold, int experience) {
        super(name, mp, strength, agility, dexterity, gold, experience,
              EnumSet.of(Stat.DEXTERITY, Stat.AGILITY));
    }
}
