//防具，裝備後能減少受到的傷害，也可以修理
public class Armor extends Item { //繼承Item，沿用物品的名字、價格、等級和使用次數
    private final int damageReduction; //防具提供的減傷值，建立後不能更改

    public Armor(String name, int price, int level, int damageReduction) {
        super(name, price, level, 20); //呼叫Item建構子，最後的20是傳給父類別的使用次數設定
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() { return damageReduction; } //讓Hero取得防具減傷值

    @Override public boolean canRepair() { return true; } //覆寫父類別方法，表示防具可以修理

    @Override public String describe() { //回傳防具資訊
        return super.describe() + " | defense " + damageReduction; //先取得父類別的物品描述，再加上減傷值
    }

    @Override public Armor copy() { //建立一件相同設定的新防具
        return new Armor(getName(), getPrice(), getRequiredLevel(), damageReduction); //重新呼叫建構子，使用次數也會重新初始化
    }
}