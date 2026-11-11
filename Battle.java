import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//Manages battle rounds, target selection, hero and monster turns, and victory rewards.
//控制回合與目標選擇 角色自己管理能力與HP 道具自己定義效果 安排戰鬥流程 英雄行動、怪物反擊、回合恢復，以及勝利後發獎勵
public class Battle {
    private final Party party;
    private final List<Monster> monsters; //宣告怪物清單
    private final ConsoleUI ui;
    private final SelectionMenus menus;
    private final World world;
    private final Random random;

    public Battle(Party party, List<Monster> monsters, ConsoleUI ui,
                  SelectionMenus menus, World world, Random random) {
        this.party = party;
        this.monsters = new ArrayList<>(monsters);//建立清單並存進欄位
        this.ui = ui;
        this.menus = menus;
        this.world = world;
        this.random = random;
    }
    public boolean start() {  //開始一場戰鬥，回傳戰鬥結果
        ui.setInformationDisplay(this::showInformation);
        try {
            return runBattle();
        } finally { //離開戰鬥時恢復原本的顯示
            ui.setInformationDisplay(party::display);
        }
    }
    private boolean runBattle() {  //負責真正執行戰鬥流程
        System.out.println("\nMonsters appeared!");
        showInformation();
        int round = 1;
        while (!party.isDefeated() && !livingMonsters().isEmpty()) {
            System.out.println("\n=== Round " + round++ + " ===");
            for (Hero hero : party.getHeroes()) {
                if (hero.isFainted()) { continue; }
                heroTurn(hero);
                if (livingMonsters().isEmpty()) { rewardVictory(); return true; }
            }
            for (Monster monster : livingMonsters()) {  //怪物依序反擊
                List<Hero> targets = party.living();  //逐一取出還沒被打敗的怪物
                if (targets.isEmpty()) { break; }
                Hero target = targets.get(random.nextInt(targets.size()));  //隨機挑一位英雄
                if (random.nextDouble() < target.getDodgeChance()) {  //閃過了
                    System.out.println(target.getName() + " dodged " + monster.getName() + "!");
                } else {
                    int actualDamage = target.receiveAttack(monster.getAttackDamage()); //先取得怪物的攻擊傷害，再交給英雄的 receiveAttack() 處理扣血，回傳的實際傷害存到actualDamage
                    System.out.println(monster.getName() + " attacked " + target.getName()  //印出傷害數字和英雄剩餘 HP
                        + " for " + actualDamage + " damage! HP: " + target.getHp());
                    if (target.isFainted()) { System.out.println(target.getName() + " fainted!"); }//如果被攻擊的英雄昏倒了，就印出他的名字和昏倒訊息。
                }
            }
            if (!party.isDefeated()) {    //怪物都行動完後，如果隊伍還沒全倒，就讓存活的英雄恢復 HP、MP
                for (Hero hero : party.living()) {
                    hero.recoverAfterRound();
                    System.out.println("After recovery - " + hero.getName() + " HP: " + hero.getHp() + ", MP: " + hero.getMp());
                }
            }
        }
        System.out.println("All heroes fainted. Game over.");
        return false;
    }
    private List<Monster> livingMonsters() {//找出還沒被打敗的怪物。
        List<Monster> living = new ArrayList<>();
        for (Monster monster : monsters) { if (!monster.isDefeated()) { living.add(monster); } }
        return living;
    }
    private Monster chooseTarget() {  //讓玩家選擇要攻擊哪一隻怪物，並回傳那隻怪物的參考
        List<Monster> living = livingMonsters();
        for (int i = 0; i < living.size(); i++) {
            System.out.println((i + 1) + ". " + living.get(i).describe());
        }
        int n = ui.number("Target (0 cancels): ", 0, living.size()); //請玩家選編號
        return n == 0 ? null : living.get(n - 1); //:否則取得選中的怪物
    }
    private void showInformation() {
        party.display();
        for (Monster monster : monsters) { System.out.println(monster.describe()); }
    }
    
    private void heroTurn(Hero hero) { //處理某一位英雄的行動回合
        while (true) {
            System.out.println("\n" + hero.getName() + " | HP " + hero.getHp() + " MP " + hero.getMp());
            System.out.println("1. Attack  2. Cast spell  3. Drink potion  4. Equip"); //1. 攻擊  2. 施法  3. 喝藥水  4. 裝備
            System.out.println("I. Information  P. Map  H. Help  Q. Quit");//I. 資訊  P. 地圖  H. 說明  Q. 退出
            String choice = ui.read("Choose: ").toLowerCase();
            switch (choice) {
                case "i": showInformation(); break;  //在Battle
                case "p": world.display(); break;//在world
                case "h": ui.help(); break;//在consoleui
                case "1":  //普通攻擊
                    Monster target = chooseTarget();
                    if (target == null) { break; }
                    int damage = hero.getAttackDamage();
                    hero.wearWeapon(); //消耗一次已裝備武器的使用次數
                    hit(hero, target, damage, null);
                    return;
                case "2": //選法術 → 檢查能不能用 → 選怪物 → 消耗魔力並攻擊
                    Item selected = menus.item(hero.getInventory().ofType(Spell.class)); //取得英雄的背包篩選出背包中的法術
                    if (!(selected instanceof Spell)) { break; }  //如果不是法術，就離開 switch
                    Spell spell = (Spell) selected;
                    if (!hero.canUse(spell) || hero.getMp() < spell.getManaCost()) {  //英雄能不能使用這個法術，以及魔力夠不夠
                        System.out.println("Cannot cast: check level, mana, or remaining uses."); break;
                    }
                    Monster spellTarget = chooseTarget();  //讓玩家選擇法術要攻擊的怪物，並把結果存進 spellTarget
                    if (spellTarget == null) { break; }
                    if (hero.prepareSpell(spell)) { //確認目標後，消耗施法資源，再執行攻擊。
                        hit(hero, spellTarget, spell.damageFor(hero.getDexterity()), spell); //3根據英雄靈巧度算出的法術傷害
                        return;
                    }
                    break;
                case "3": if (menus.drink(hero)) { return; } break; //喝藥水
                case "4": if (menus.equip(hero)) { return; } break; //換裝備
                default: System.out.println("Invalid choice. No turn consumed.");
            }
        }
    }
    private void hit(Hero hero, Monster target, int damage, Spell spell) {  //判斷怪物是否閃避，沒閃過就處理扣血，並顯示結果
        if (random.nextDouble() < target.getDodgeChance()) { //判斷怪物有沒有閃過
            System.out.println(target.getName() + " dodged! Mana/uses are still consumed.");
            return;
        }
        int actualDamage = target.receiveAttack(damage);//把傷害交給怪物的 receiveAttack()，處理防禦與扣血，再把實際傷害存到 actualDamage
        System.out.println(hero.getName() + (spell == null ? " attacked " : " cast " + spell.getName() + " on ")
            + target.getName() + " for " + actualDamage + " damage! HP: " + target.getHp());
        if (spell != null && !target.isDefeated()) {//處理法術效果及擊敗提示 有使用法術，而且怪物還沒被打敗
            spell.weaken(target);
            System.out.println("Spell effect applied: target's affected skill reduced by 10%.");
        }//最後檢查怪物是否被打敗，有就印出提示
        if (target.isDefeated()) { System.out.println(target.getName() + " was defeated!"); }
    }
    private void rewardVictory() { //戰鬥勝利後，復活昏倒的英雄，並給存活英雄獎勵
        int totalLevels = 0;
        for (Monster monster : monsters) { totalLevels += monster.getLevel(); } //先加總所有怪物的等級
        System.out.println("You won!");
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) { //昏倒的英雄會復活，但不獲得金錢和經驗值
                hero.revive();
                System.out.println(hero.getName() + " revived at half base HP/MP; no rewards.");
            } else {//這位英雄沒有昏倒，所以可以領取獎勵
                int gold = totalLevels * 100, experience = totalLevels * 2; //計算金錢：怪物總等級 × 100。計算經驗值：怪物總等級 × 2。
                hero.addGold(gold); //增加英雄的金錢
                hero.gainExperience(experience); //增加經驗值，足夠時會升級
                System.out.println(hero.getName() + " gained " + gold + " gold and " + experience + " XP.");
            }
        }
    }
}
