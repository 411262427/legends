import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//管理商店庫存，處理購買、賣出和裝備修理
public class Market {
    private final List<Item> stock = new ArrayList<>(); //建立商店自己的空庫存清單

    public Market(List<Item> items) {
        for (Item item : items) { stock.add(item.copy()); } //每件商品建立新物件，讓不同商店的商品各自獨立
    }

    public List<Item> getStock() { return Collections.unmodifiableList(stock); } //回傳庫存的唯讀檢視，外面不能透過它增刪商品

    public boolean buy(Hero hero, Item item) { //英雄購買商品，回傳是否成功
        //商品必須存在、在庫存裡、英雄等級足夠，而且付款成功
        if (item == null || !stock.contains(item) || hero.getLevel() < item.getRequiredLevel()
            || !hero.spendGold(item.getPrice())) { return false; } //spendGold成功時就會直接扣錢
        stock.remove(item); //從商店庫存移除
        hero.getInventory().add(item); //把同一件商品加入英雄背包，沒有另外複製
        return true;
    }

    public boolean sell(Hero hero, Item item) { //英雄賣出自己背包裡的物品
        if (item == null || !hero.getInventory().contains(item)) { return false; }
        hero.unequip(item); //如果正在裝備這件物品，先卸下
        hero.getInventory().remove(item); //從英雄背包移除
        hero.addGold(item.getSalePrice()); //把賣出所得加入英雄的金錢
        stock.add(item); //物品放進商店庫存，剩餘使用次數不會重設
        return true;
    }

    public boolean repair(Hero hero, Item item) { //支付原價的一半修理物品
        //物品必須在自己的背包、可以修理、使用次數還沒滿，而且付款成功
        if (item == null || !hero.getInventory().contains(item) || !item.canRepair()
            || item.getUses() == item.getMaxUses() || !hero.spendGold(item.getPrice() / 2)) { return false; }
        item.repair(); //恢復完整使用次數
        return true;
    }
}