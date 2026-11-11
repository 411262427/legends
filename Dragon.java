//Represents a monster with a 10% bonus to base damage.
//Dragon是攻擊傷害額外提高10%的怪物
public class Dragon extends Monster { //繼承Monster，沿用怪物的資料和戰鬥方法
    public Dragon(String name, int level, int damage, int defense, double dodge) {
        //先把傳入的傷害提高10%並向上取整，再交給父類別建構子設定
        super(name, level, GameRules.rounded(damage * 1.10), defense, dodge);
    }
}
