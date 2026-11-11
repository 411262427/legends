//Represents a monster with a 10% bonus to dodge ability.
public class Spirit extends Monster {
    public Spirit(String name, int level, int damage, int defense, double dodge) {
        super(name, level, damage, defense, dodge * 1.10);
    }
}
