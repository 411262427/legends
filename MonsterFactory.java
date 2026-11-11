import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//資料模板與戰鬥中的怪物分開，每次遭遇都產生新物件
public class MonsterFactory {
    public static class Template {
        private final String type;
        private final String name;
        private final int level;
        private final int damage;
        private final int defense;
        private final double dodge;
        public Template(String type, String name, int level, int damage, int defense, double dodge) {
            this.type = type;
            this.name = name;
            this.level = level;
            this.damage = damage;
            this.defense = defense;
            this.dodge = dodge;
        }
        private Monster create(int targetLevel) {
            double ratio = targetLevel / (double) level;
            int scaledDamage = GameRules.rounded(damage * ratio);
            int scaledDefense = GameRules.rounded(defense * ratio);
            if (type.equals("Dragons")) { return new Dragon(name, targetLevel, scaledDamage, scaledDefense, dodge); }
            if (type.equals("Exoskeletons")) { return new Exoskeleton(name, targetLevel, scaledDamage, scaledDefense, dodge); }
            return new Spirit(name, targetLevel, scaledDamage, scaledDefense, dodge);
        }
    }
    private final List<Template> templates;
    public MonsterFactory(List<Template> templates) { this.templates = new ArrayList<>(templates); }
    public List<Monster> createEncounter(Party party, Random random) {
        List<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < party.getHeroes().size(); i++) {
            monsters.add(templates.get(random.nextInt(templates.size())).create(party.highestLevel()));
        }
        return monsters;
    }
}
