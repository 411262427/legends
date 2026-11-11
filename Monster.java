//Defines shared monster attributes, combat abilities, and stat reductions caused by spells.
//怪物共用的資料、戰鬥能力，以及受到法術後的能力削弱
public abstract class Monster extends Combatant { //繼承Combatant，沿用名字、等級、HP和扣血方法
    private int baseDamage; //基礎攻擊傷害
    private int defense; //防禦值
    private double dodge; //閃避機率，例如0.2表示20%

    protected Monster(String name, int level, int baseDamage, int defense, double dodge) {
        super(name, level); //交給父類別設定名字、等級和初始HP，HP=等級*100
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodge = dodge;
    }

    public boolean isDefeated() { return isDown(); } //呼叫父類別方法，HP小於等於0就是被打敗

    @Override public int getAttackDamage() { //計算怪物實際的攻擊傷害
        return Math.max(1, GameRules.rounded(baseDamage * GameRules.ATTACK_SCALE)); //基礎傷害乘攻擊倍率，向上取整，最低為1
    }

    @Override public int getDamageReduction() { //計算怪物受到攻擊時能減少多少傷害
        return GameRules.rounded(defense * GameRules.DEFENSE_SCALE); //防禦值乘防禦倍率，再向上取整
    }

    @Override public double getDodgeChance() { return Math.min(GameRules.MAX_DODGE, dodge); } //閃避機率不能超過設定的上限

    public void reduceDamage() { baseDamage = (int) (baseDamage * 0.9); } //冰凍法術降低目前基礎傷害10%，轉成int會捨去小數
    public void reduceDefense() { defense = (int) (defense * 0.9); } //火焰法術降低目前防禦值10%，轉成int會捨去小數
    public void reduceDodge() { dodge *= 0.9; } //閃電法術降低目前閃避機率10%，例如20%變成18%

    @Override public String describe() { //回傳怪物資訊的文字，不直接印出
        return getClass().getSimpleName() + " " + getName() + " | Lv " + getLevel() //取得實際子類別名稱、名字和等級
            + " HP " + getHp() + " Attack " + getAttackDamage()
            + " Defense " + defense + " (reduces " + getDamageReduction() + ")" //同時顯示防禦值和換算後的減傷值
            + " Dodge " + Math.round(getDodgeChance() * 100) + "%"; //把閃避機率轉成百分比顯示
    }
}