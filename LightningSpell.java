//Represents a lightning spell that deals damage and reduces the target's dodge chance.
//閃電法術，造成傷害並降低怪物閃避機率
public class LightningSpell extends Spell { //繼承Spell，沿用法術的資料和傷害計算
    public LightningSpell(String name, int price, int level, int damage, int manaCost) {
        super(name, price, level, damage, manaCost); //交給父類別建構子設定名字、價格、等級、傷害和魔力消耗
    }

    @Override public void weaken(Monster target) { //覆寫法術的削弱效果，target是被攻擊的怪物
        target.reduceDodge(); //呼叫怪物的方法降低閃避機率
    }

    @Override public LightningSpell copy() { //建立一個相同設定的新閃電法術物件
        return new LightningSpell(getName(), getPrice(), getRequiredLevel(), getBaseDamage(), getManaCost());
    }
}