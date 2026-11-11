//Defines shared attributes and behaviors for heroes and monsters.
//英雄與怪物共用的父類別，集中管理它們都有的資料與行為
public abstract class Combatant {  //抽象類別，不能直接new Combatant
    private final String name; 
    private int level; 
    private int hp; 

    protected Combatant(String name, int level) { //讓子類別透過super呼叫建構子
        this.name = name;
        this.level = level;
        this.hp = level * 100; //初始HP=等級*100
    }

    //讓其他類別取得名字、等級和目前HP
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public boolean isDown() { return hp <= 0; } //HP小於等於0就表示已倒下

    //protected讓同套件的類別和子類別能使用這些方法
    protected void setLevel(int level) { this.level = level; } //設定等級，不會自動調整HP
    protected void setHp(int hp) { this.hp = Math.max(0, hp); } //設定HP，最低為0
    protected void heal(int amount) { //補血，負數補血量當成0
        hp = GameRules.rounded((double) hp + Math.max(0, amount)); //補血後交給rounded限制數值，沒有用等級*100當上限
    }

    public int takeDamage(int damage) { //實際扣HP，並回傳扣掉多少
        //傷害不能是負數，也不能扣超過目前HP
        int actualDamage = Math.min(hp, Math.max(0, damage));
        hp -= actualDamage;
        return actualDamage; //例如剩10HP卻受到30傷害，實際只扣10
    }

    public int receiveAttack(int rawDamage) { //先計算減傷，再交給takeDamage扣HP
        //原始傷害扣掉防禦減傷，命中傷害最低為1，實際扣血仍不超過剩餘HP
        return takeDamage(Math.max(1, rawDamage - getDamageReduction()));
    }

    //只宣告方法，不在這裡寫公式，交給具體子類別提供實作
    public abstract int getAttackDamage(); //回傳攻擊傷害
    public abstract int getDamageReduction(); //回傳可以減少的傷害
    public abstract double getDodgeChance(); //回傳閃避機率
    public abstract String describe(); //回傳角色資訊的描述文字
}