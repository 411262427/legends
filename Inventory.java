import java.util.ArrayList;
import java.util.Collections; //排序、打亂
import java.util.List;

//背包保存物品，其他類別透過方法存取，不直接修改內部清單
public class Inventory {
    private final List<Item> items = new ArrayList<>(); //建立空物品清單，final不影響清單內容增減

    public void add(Item item) { //把物品加入背包
        if (item == null || items.contains(item)) { //物品不存在或已在背包裡，就拒絕加入
            throw new IllegalArgumentException("Invalid or duplicate item.");
        }
        items.add(item);
    }

    public boolean contains(Item item) { return items.contains(item); } //檢查背包是否有這件物品
    public boolean remove(Item item) { return items.remove(item); } //移除物品，成功回傳true，找不到回傳false
    public List<Item> getItems() { return Collections.unmodifiableList(items); } //回傳唯讀檢視，不能透過它增刪或替換物品，但物品本身仍能改變

    public <T extends Item> List<T> ofType(Class<T> type) { //篩選指定種類的物品，T只能是Item或它的子類別
        //例如傳入Spell.class，T就是Spell，回傳型別就是List<Spell>
        List<T> result = new ArrayList<>(); //建立空清單，準備放符合種類的物品
        for (Item item : items) {
            if (type.isInstance(item)) { result.add(type.cast(item)); } //確認物品符合指定型別，再轉型成T加入清單
        }
        return result; //回傳篩選結果，沒有複製物品，也沒有改動原本的背包清單
    }
}
