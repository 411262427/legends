import java.util.List;

//顯示選項與背包操作，不負責交易或戰鬥規則
public class SelectionMenus {
    private final ConsoleUI ui;
    public SelectionMenus(ConsoleUI ui) { this.ui = ui; }

    public Hero hero(Party party) {
        List<Hero> heroes = party.getHeroes();
        for (int i = 0; i < heroes.size(); i++) {
            System.out.println((i + 1) + ". " + heroes.get(i).getName());
        }
        int n = ui.number("Hero (0 cancels): ", 0, heroes.size());
        return n == 0 ? null : heroes.get(n - 1);
    }
    public Item item(List<? extends Item> items) {
        if (items.isEmpty()) { System.out.println("No items available."); return null; }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).describe());
        }
        int n = ui.number("Item (0 cancels): ", 0, items.size());
        return n == 0 ? null : items.get(n - 1);
    }
    public boolean equip(Hero hero) {
        Item chosen = item(hero.getInventory().getItems());
        boolean success = false;
        if (chosen instanceof Weapon) {
            Weapon weapon = (Weapon) chosen;
            int hands = ui.number("Hands (" + weapon.getRequiredHands() + "-2): ", weapon.getRequiredHands(), 2);
            success = hero.equipWeapon(weapon, hands);
        } else if (chosen instanceof Armor) {
            success = hero.equipArmor((Armor) chosen);
        }
        System.out.println(success ? "Equipped." : "Not equipped: select usable equipment of your level.");
        return success;
    }
    public boolean drink(Hero hero) {
        Item chosen = item(hero.getInventory().ofType(Potion.class));
        if (!(chosen instanceof Potion)) { return false; }
        boolean success = hero.usePotion((Potion) chosen);
        System.out.println(success ? "Potion used." : "Cannot use this potion.");
        return success;
    }
    public void inventory(Party party) {
        Hero hero = hero(party);
        if (hero == null) { return; }
        while (true) {
            System.out.println("\n" + hero.describe());
            System.out.println("1. List inventory  2. Equip  3. Drink potion  0. Back");
            int action = ui.number("Choose: ", 0, 3);
            if (action == 0) { return; }
            if (action == 1) {
                for (Item item : hero.getInventory().getItems()) { System.out.println(item.describe()); }
            } else if (action == 2) { equip(hero); }
            else { drink(hero); }
        }
    }
}
