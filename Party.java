import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party {
    private final List<Hero> heroes = new ArrayList<>(); //先建立一個空的英雄清單
    public void add(Hero hero) {
        if (hero == null || heroes.contains(hero) || heroes.size() >= GameRules.MAX_PARTY) {
            throw new IllegalArgumentException("Invalid party member.");
        }
        heroes.add(hero); //把這位英雄加入清單
    }
    public List<Hero> getHeroes() { return Collections.unmodifiableList(heroes); }  //只能取得英雄清單
    public List<Hero> living() {
        List<Hero> result = new ArrayList<>();
        for (Hero hero : heroes) { if (!hero.isFainted()) { result.add(hero); } }
        return result;
    }
    public int highestLevel() {  //找出隊伍裡最高的英雄等級
        int max = 1;
        for (Hero hero : heroes) { max = Math.max(max, hero.getLevel()); }
        return max;
    }
    public boolean isDefeated() { return living().isEmpty(); }
    public void display() { for (Hero hero : heroes) { System.out.println(hero.describe()); } }

}
