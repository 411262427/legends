//所有物品共同的資料，每個物件代表一件獨立物品
public abstract class Item { //抽象類別，交給武器、防具、藥水和法術等子類別繼承
    private final String name; //物品名字
    private final int price; //購買價格
    private final int requiredLevel; //使用物品需要的最低等級
    private final int maxUses; //最多使用次數，建立後不能更改
    private int uses; //目前剩餘使用次數，使用後會減少

    protected Item(String name, int price, int requiredLevel, int maxUses) { //讓子類別透過super設定共同資料
        this.name = name;
        this.price = price;
        this.requiredLevel = requiredLevel;
        this.maxUses = maxUses;
        this.uses = maxUses; //新物品一開始有完整使用次數
    }

    //讓其他類別透過方法取得物品資料
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getRequiredLevel() { return requiredLevel; }
    public boolean isUsable() { return uses > 0; } //還有剩餘使用次數才可以使用
    public int getUses() { return uses; }
    public int getMaxUses() { return maxUses; }
    public int getSalePrice() { return price / 2; } //賣出價格是原價的一半，整數除法會捨去小數
    public boolean canRepair() { return false; } //預設不能修理，可修理的子類別再覆寫成true

    public void repair() { //修復物品的使用次數
        if (!canRepair()) { throw new IllegalStateException("Cannot repair consumables."); } //不能修理就丟出例外
        uses = maxUses; //恢復到完整使用次數
    }

    public void consumeUse() { //消耗一次使用次數
        if (!isUsable()) { throw new IllegalStateException("Item is exhausted."); } //已用完就不能繼續消耗
        uses--;
    }

    public String describe() { //回傳物品資訊的文字，不直接印出
        //取得實際子類別名稱，再接上名字、價格、所需等級和剩餘使用次數
        return getClass().getSimpleName() + " " + name + " | $" + price
            + " | Lv " + requiredLevel + " | uses " + uses + "/" + maxUses;
    }

    public abstract Item copy(); //交給具體子類別實作，建立一件相同設定的新物品
}