//Exoskeleton是防禦值額外提高10%的怪物
public class Exoskeleton extends Monster { //繼承Monster，沿用怪物的資料和戰鬥方法
    public Exoskeleton(String name, int level, int damage, int defense, double dodge) {
        //先把傳入的防禦值提高10%並向上取整，再交給父類別建構子設定
        super(name, level, damage, GameRules.rounded(defense * 1.10), dodge);
    }
}