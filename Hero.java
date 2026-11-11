import java.util.EnumSet;
//Defines shared hero attributes and manages inventory, equipment, combat abilities, and leveling up.
//存英雄資料，也負責管理英雄的能力與狀態變化
public abstract class Hero extends Combatant { //抽象類別不能直接new，繼承Combatant
    private int mp; //目前魔力
    private int baseMana; //基礎魔力，升級和復活時會用到
    private int strength; //力量
    private int agility; //敏捷度
    private int dexterity; //靈巧度
    private int gold; //持有金錢
    private int experience; //累積經驗值
    private int bonusDefense; //額外防禦值
    private final EnumSet<Stat> favored; //保存英雄擅長的能力，初始化和升級時會額外加成
    private final Inventory inventory = new Inventory(); //建立英雄自己的空背包
    private Weapon equippedWeapon; //指向目前裝備的武器，沒裝備時是null
    private Armor equippedArmor; //指向目前裝備的防具，沒裝備時是null
    private int weaponHands; //記錄使用幾隻手持武器

    protected Hero(String name, int mp, int strength, int agility,
                   int dexterity, int gold, int experience, EnumSet<Stat> favored) {
        super(name, 1); //先呼叫父類別建構子，設定名字、等級1和初始HP100
        this.mp = mp;
        this.baseMana = mp; //記住基礎魔力，不會因為施法扣MP而一起減少
        this.strength = strength;
        this.agility = agility;
        this.dexterity = dexterity;
        this.gold = gold;
        this.experience = experience;
        this.favored = favored.clone(); //複製一份擅長能力的集合
        //擅長的能力在初始時額外提高5%，rounded會向上取整
        if (favored.contains(Stat.STRENGTH)) { this.strength = GameRules.rounded(strength * 1.05); }
        if (favored.contains(Stat.AGILITY)) { this.agility = GameRules.rounded(agility * 1.05); }
        if (favored.contains(Stat.DEXTERITY)) { this.dexterity = GameRules.rounded(dexterity * 1.05); }
    }

    //讓其他類別透過方法取得英雄資料
    public int getMp() { return mp; }
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getDexterity() { return dexterity; }
    public int getGold() { return gold; }
    public int getExperience() { return experience; }
    public Inventory getInventory() { return inventory; } //回傳背包物件的參考
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public boolean isFainted() { return isDown(); } //呼叫父類別方法，HP小於等於0就是昏倒

    //裝備只能來自英雄自己的背包，回傳是否裝備成功
    public boolean equipWeapon(Weapon weapon, int hands) {
        //沒有武器、不在背包、不能使用、等級不足或手數不合，就裝備失敗
        if (weapon == null || !inventory.contains(weapon) || !weapon.isUsable()
            || getLevel() < weapon.getRequiredLevel() || hands < weapon.getRequiredHands()
            || hands > 2) { return false; }
        equippedWeapon = weapon; //記住目前裝備的武器，武器仍留在背包裡
        weaponHands = hands; //記住持握手數
        return true;
    }

    public boolean equipArmor(Armor armor) { //裝備防具，回傳是否成功
        //防具必須在自己的背包、還能使用，而且英雄等級足夠
        if (armor == null || !inventory.contains(armor) || !armor.isUsable()
            || getLevel() < armor.getRequiredLevel()) { return false; }
        equippedArmor = armor; //把目前防具換成傳進來的這件
        return true;
    }

    public void unequip(Item item) { //卸下裝備，不會把物品移出背包
        //==比較是不是同一個物件，是目前武器就清空武器欄位和手數
        if (equippedWeapon == item) { equippedWeapon = null; weaponHands = 0; }
        if (equippedArmor == item) { equippedArmor = null; } //是目前防具就清空防具欄位
    }

    public void wearWeapon() { //攻擊時消耗武器使用次數，wear在這裡是磨損
        if (equippedWeapon != null && equippedWeapon.isUsable()) { equippedWeapon.consumeUse(); }
    }

    @Override public int getAttackDamage() { //實作父類別的方法，計算普通攻擊傷害
        double weaponDamage = 0; //沒武器時傷害是0，仍然可以靠力量攻擊
        if (equippedWeapon != null && equippedWeapon.isUsable()) {
            weaponDamage = equippedWeapon.getDamage(); //取得可用武器的傷害
            //單手武器用雙手拿，武器傷害提高20%
            if (equippedWeapon.getRequiredHands() == 1 && weaponHands == 2) { weaponDamage *= 1.2; }
        }
        //攻擊傷害=(力量+武器傷害)*攻擊倍率，向上取整，最低為1
        return Math.max(1, GameRules.rounded((strength + weaponDamage) * GameRules.ATTACK_SCALE));
    }

    @Override public int getDamageReduction() { //計算受到攻擊時能減少多少傷害
        int defense = bonusDefense; //先放入額外防禦值
        if (equippedArmor != null && equippedArmor.isUsable()) {
            defense += equippedArmor.getDamageReduction(); //有可用防具就加上防具減傷值
        }
        return GameRules.rounded(defense * GameRules.DEFENSE_SCALE); //總防禦值乘倍率後向上取整
    }

    @Override public double getDodgeChance() { //計算英雄閃避機率
        //敏捷除以10000轉成機率，最多只能到MAX_DODGE
        return Math.min(GameRules.MAX_DODGE, agility / 10000.0);
    }

    @Override public int receiveAttack(int damage) { //受到攻擊時扣血，並消耗防具使用次數
        int actualDamage = super.receiveAttack(damage); //交給父類別計算減傷和扣HP
        if (equippedArmor != null && equippedArmor.isUsable()) { equippedArmor.consumeUse(); }
        return actualDamage; //回傳實際受到的傷害
    }

    public boolean canUse(Item item) { //檢查英雄能不能使用這個物品
        //英雄沒昏倒、物品存在、在背包、能使用，而且等級足夠，才回傳true
        return !isFainted() && item != null && inventory.contains(item)
            && item.isUsable() && getLevel() >= item.getRequiredLevel();
    }

    public boolean usePotion(Potion potion) { //讓英雄喝藥水，回傳是否成功
        if (!canUse(potion)) { return false; } //不能使用就停止
        potion.applyTo(this); //把藥水效果套用到這位英雄自己
        potion.consumeUse(); //消耗一次使用次數
        inventory.remove(potion); //喝完就從背包移除
        return true;
    }

    public boolean prepareSpell(Spell spell) { //檢查能否施法，並消耗魔力和使用次數
        if (!canUse(spell) || mp < spell.getManaCost()) { return false; }
        mp -= spell.getManaCost(); //扣除法術需要的魔力
        spell.consumeUse(); //消耗一次法術使用次數
        if (!spell.isUsable()) { inventory.remove(spell); } //不能再使用才從背包移除
        return true; //準備成功，實際攻擊由Battle的hit處理
    }

    public void increaseStat(Stat stat, int amount) { //依照能力種類增加數值
        if (amount <= 0) { return; } //增加量不是正數就不處理
        switch (stat) {
            case HEALTH: heal(amount); break; //呼叫父類別方法補HP
            case MANA: mp = GameRules.rounded((double) mp + amount); break;
            case STRENGTH: strength = GameRules.rounded((double) strength + amount); break;
            case AGILITY: agility = GameRules.rounded((double) agility + amount); break;
            case DEXTERITY: dexterity = GameRules.rounded((double) dexterity + amount); break;
            case DEFENSE: bonusDefense = GameRules.rounded((double) bonusDefense + amount); break;
            default: throw new IllegalArgumentException("Unknown stat.");
        }
    }

    public boolean spendGold(int amount) { //花錢，回傳是否付款成功
        if (amount < 0 || gold < amount) { return false; } //金額不能是負數，也不能超過持有金錢
        gold -= amount;
        return true;
    }

    public void addGold(int amount) { //增加金錢，負數當成0，不會用這個方法扣錢
        gold = GameRules.rounded((double) gold + Math.max(0, amount));
    }

    public void gainExperience(int amount) { //增加經驗值，並檢查是否升級
        experience = GameRules.rounded((double) experience + Math.max(0, amount));
        //經驗值累積不扣除，從目前等級升到下一級所需的累積經驗是level*(level+1)*5
        while (getLevel() < 100 && experience >= getLevel() * (getLevel() + 1) * 5) {
            setLevel(getLevel() + 1); //等級加1，經驗足夠時可以連續升級
            strength = grow(strength, Stat.STRENGTH);
            agility = grow(agility, Stat.AGILITY);
            dexterity = grow(dexterity, Stat.DEXTERITY);
            setHp(getLevel() * 100); //升級後HP直接設為新等級*100
            baseMana = GameRules.rounded(baseMana * 1.1); //基礎魔力提高10%
            mp = baseMana; //目前MP設成升級後的基礎魔力
            System.out.println(getName() + " reached level " + getLevel() + "!");
        }
    }

    private int grow(int value, Stat stat) { //計算升級後的能力值
        //擅長的能力提高10%，其他能力提高5%
        return GameRules.rounded(value * (favored.contains(stat) ? 1.10 : 1.05));
    }

    public void recoverAfterRound() { //每回合結束時，存活英雄恢復目前HP和MP的10%
        if (!isFainted()) {
            heal(GameRules.rounded(getHp() * 0.1)); //依目前HP計算補血量
            mp = GameRules.rounded(mp * 1.1); //目前MP增加10%
        }
    }

    public void revive() { //戰鬥勝利後復活昏倒的英雄
        if (isFainted()) { setHp(getLevel() * 50); mp = baseMana / 2; } //HP設為等級*50，MP設為基礎魔力的一半
    }

    @Override public String describe() { //回傳英雄資訊的描述文字，這裡沒有直接印出
        //取得實際子類別名稱，再接上名字、等級和各項數值
        return getClass().getSimpleName() + " " + getName() + " | Lv " + getLevel()
            + " HP " + getHp() + " MP " + mp + " STR " + strength + " AGI " + agility
            + " DEX " + dexterity + " Gold " + gold + " XP " + experience
            + " | Dodge " + Math.round(getDodgeChance() * 100) + "%" //把閃避機率轉成百分比顯示
            //沒裝備就顯示none，有裝備就顯示物品資訊，武器另外顯示持握手數
            + "\n  Weapon: " + (equippedWeapon == null ? "none" : equippedWeapon.describe() + " held " + weaponHands + " hand(s)")
            + "\n  Armor: " + (equippedArmor == null ? "none" : equippedArmor.describe());
    }
}
