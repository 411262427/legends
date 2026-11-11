import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

//Reads files and creates default objects without modifying the data files.
public class DataLoader {
    private final Path directory;
    public DataLoader(Path directory) { this.directory = directory; }

    private List<String[]> rows(String filename, int columns) throws IOException {
        List<String> lines = Files.readAllLines(directory.resolve(filename), StandardCharsets.UTF_8);
        List<String[]> result = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) { continue; }
            String[] cells = line.split("\\s+", columns);
            if (cells.length != columns) {
                throw new IOException(filename + ": invalid row " + (i + 1));
            }
            result.add(cells);
        }
        if (result.isEmpty()) { throw new IOException(filename + ": no data."); }
        return result;
    }
    private int integer(String text) { return Integer.parseInt(text); }

    public List<Hero> loadHeroes() throws IOException {
        List<Hero> heroes = new ArrayList<>();
        for (String type : new String[]{"Warriors", "Sorcerers", "Paladins"}) {
            for (String[] r : rows(type + ".txt", 7)) {
                int mp = integer(r[1]), strength = integer(r[2]), agility = integer(r[3]);
                int dexterity = integer(r[4]), gold = integer(r[5]), experience = integer(r[6]);
                if (type.equals("Warriors")) {
                    heroes.add(new Warrior(r[0], mp, strength, agility, dexterity, gold, experience));
                } else if (type.equals("Sorcerers")) {
                    heroes.add(new Sorcerer(r[0], mp, strength, agility, dexterity, gold, experience));
                } else {
                    heroes.add(new Paladin(r[0], mp, strength, agility, dexterity, gold, experience));
                }
            }
        }
        return heroes;
    }
    public List<Item> loadItems() throws IOException {
        List<Item> items = new ArrayList<>();
        for (String[] r : rows("Weaponry.txt", 5)) {
            items.add(new Weapon(r[0], integer(r[1]), integer(r[2]), integer(r[3]), integer(r[4])));
        }
        for (String[] r : rows("Armory.txt", 4)) {
            items.add(new Armor(r[0], integer(r[1]), integer(r[2]), integer(r[3])));
        }
        for (String[] r : rows("Potions.txt", 5)) {
            EnumSet<Stat> effects = EnumSet.noneOf(Stat.class);
            String effectText = r[4].toUpperCase();
            for (Stat stat : Stat.values()) {
                if (effectText.contains(stat.name())) { effects.add(stat); }
            }
            if (effects.isEmpty()) { throw new IOException("Unknown potion effect: " + r[4]); }
            items.add(new Potion(r[0], integer(r[1]), integer(r[2]), integer(r[3]), effects));
        }
        //補齊資料檔缺少的單一能力藥水
        items.add(new Potion("Dexterity_Potion", 200, 1, 75, EnumSet.of(Stat.DEXTERITY)));
        items.add(new Potion("Agility_Potion", 200, 1, 75, EnumSet.of(Stat.AGILITY)));
        items.add(new Potion("Mana_Potion", 150, 1, 100, EnumSet.of(Stat.MANA)));
        for (String type : new String[]{"Ice", "Fire", "Lightning"}) {
            for (String[] r : rows(type + "Spells.txt", 5)) {
                int price = integer(r[1]), level = integer(r[2]), damage = integer(r[3]), mana = integer(r[4]);
                if (type.equals("Ice")) { items.add(new IceSpell(r[0], price, level, damage, mana)); }
                else if (type.equals("Fire")) { items.add(new FireSpell(r[0], price, level, damage, mana)); }
                else { items.add(new LightningSpell(r[0], price, level, damage, mana)); }
            }
        }
        return items;
    }
    public MonsterFactory loadMonsterFactory() throws IOException {
        List<MonsterFactory.Template> templates = new ArrayList<>();
        for (String type : new String[]{"Dragons", "Exoskeletons", "Spirits"}) {
            for (String[] r : rows(type + ".txt", 5)) {
                templates.add(new MonsterFactory.Template(type, r[0], integer(r[1]),
                    integer(r[2]), integer(r[3]), integer(r[4]) / 100.0));
            }
        }
        return new MonsterFactory(templates);
    }
}
