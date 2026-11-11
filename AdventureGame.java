import java.util.Random;
//遊戲開始後玩家會一直做的事情集中管理
public class AdventureGame {
    private final Party party;
    private final World world;
    private final MonsterFactory factory;
    private final ConsoleUI ui;
    private final SelectionMenus menus;
    private final MarketMenu marketMenu;
    private final Random random;

    public AdventureGame(Party party, World world, MonsterFactory factory, ConsoleUI ui, Random random) {
        this.party = party;
        this.world = world;
        this.factory = factory;
        this.ui = ui;
        this.random = random;
        this.menus = new SelectionMenus(ui);  //選清單物件
        this.marketMenu = new MarketMenu(ui, menus);  //處理商店流程
    }
    public void play() {  //啟動並持續執行主遊戲流程
        ui.help(); 
        world.display();

        while (!party.isDefeated()) {  //只要還沒全死 
            String command = ui.read("\nCommand (H help): ").toLowerCase();
            switch (command) {
                case "i": party.display(); break;
                case "p": world.display(); break;
                case "h": ui.help(); break;
                case "b": menus.inventory(party); break;  //	開啟隊伍背包選單
                case "m":  //開啟商店
                    if (world.currentTile().getMarket() == null) {
                        System.out.println("There is no market on this tile.");
                    } else { marketMenu.open(world.currentTile().getMarket(), party); }  //把「目前的商店」和「玩家隊伍」傳給 open()
                    break;
                case "w": case "a": case "s": case "d":
                    if (!world.move(command)) { System.out.println("Cannot move there."); break; }
                    world.display();  //顯示移動後的地圖
                    if (world.currentTile().getType() == Tile.Type.COMMON 
                        && random.nextDouble() < GameRules.ENCOUNTER_CHANCE) {  //抽一個隨機數，決定這次要不要遇怪
                        Battle battle = new Battle(party, factory.createEncounter(party, random), ui, menus, world, random); //建立一場戰鬥，並把戰鬥需要的東西交給它
                        if (!battle.start()) { return; }
                    }
                    break;
                default: System.out.println("Unknown command. H shows help.");
            }
        }
    }
}
