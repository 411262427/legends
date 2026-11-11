//Displays the market menu and handles player choices for trading, repairs, and item use.
//顯示商店選單，讓玩家選擇買賣、修理、裝備和喝藥水
public class MarketMenu {
    private final ConsoleUI ui; //共用的輸入輸出工具
    private final SelectionMenus menus; //共用的英雄和物品選擇工具

    public MarketMenu(ConsoleUI ui, SelectionMenus menus) { this.ui = ui; this.menus = menus; } //保存外面傳進來的物件參考

    public void open(Market market, Party party) { //開啟指定商店，讓隊伍裡的英雄進行操作
        Hero hero = menus.hero(party); //先選擇要逛商店的英雄
        if (hero == null) { return; } //取消選擇就結束商店選單
        while (true) {
            System.out.println("\nMarket | " + hero.getName() + " | Gold " + hero.getGold()); //顯示目前英雄和持有金錢
            System.out.println("1. Buy  2. Sell (half price)  3. Repair (half price)");
            System.out.println("4. Equip  5. Drink potion  6. Change hero  0. Leave");
            int action = ui.number("Choose: ", 0, 6); //只接受0到6的整數
            if (action == 0) { return; } //離開商店，結束open方法

            if (action == 1) { //購買商品
                Item item = menus.item(market.getStock()); //從商店庫存選商品
                if (item != null) { //沒有取消才進行購買
                    System.out.println(market.buy(hero, item) ? "Purchased." : "Cannot buy: check gold, level, or remaining uses."); //呼叫Market處理購買，再依結果顯示訊息
                }
            } else if (action == 2) { //賣出物品
                Item item = menus.item(hero.getInventory().getItems()); //從目前英雄的背包選物品
                if (item != null) {
                    int price = item.getSalePrice(); //先取得賣出價格，成功後用來顯示
                    System.out.println(market.sell(hero, item) ? "Sold for " + price + "." : "Sale failed.");
                }
            } else if (action == 3) { //修理物品
                Item item = menus.item(hero.getInventory().getItems());
                if (item != null) {
                    System.out.println(market.repair(hero, item) ? "Repaired." : "Cannot repair: check gold, ownership, or durability."); //交給Market檢查條件、扣錢和修理
                }
            } else if (action == 4) { menus.equip(hero); } //開啟目前英雄的裝備選單
            else if (action == 5) { menus.drink(hero); } //讓目前英雄選藥水喝
            else { //前面已處理0到5，所以這裡是6，更換操作的英雄
                Hero selected = menus.hero(party);
                if (selected != null) { hero = selected; } //有選到才更換，取消就繼續使用原本的英雄
            }
            //做完操作會回到迴圈開頭，重新顯示英雄金錢和商店選單
        }
    }
}