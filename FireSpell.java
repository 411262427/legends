//Represents a fire spell that deals damage and reduces the target's defense.
//火焰法術，造成傷害並降低怪物防禦
public class FireSpell extends Spell { //繼承Spell，沿用法術的資料和傷害計算
    public FireSpell(String name, int price, int level, int damage, int manaCost) {
        super(name, price, level, damage, manaCost); //交給父類別建構子設定名字、價格、等級、傷害和魔力消耗
    }

    @Override public void weaken(Monster target) { //覆寫法術的削弱效果，target是被攻擊的怪物
        target.reduceDefense(); //呼叫怪物的方法降低防禦
    }

    @Override public FireSpell copy() { //建立一個相同設定的新火焰法術物件
        return new FireSpell(getName(), getPrice(), getRequiredLevel(), getBaseDamage(), getManaCost());
    }
}
