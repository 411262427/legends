//Represents a hero with favored strength and dexterity.
import java.util.EnumSet;

public class Paladin extends Hero {
    public Paladin(String name, int mp, int strength, int agility,
                   int dexterity, int gold, int experience) {
        super(name, mp, strength, agility, dexterity, gold, experience,
              EnumSet.of(Stat.STRENGTH, Stat.DEXTERITY));
    }
}
