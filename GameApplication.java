import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
//Loads data, lets the player select heroes, and starts the game.
//應用程式入口流程：載入資料、選英雄、建立遊戲
public class GameApplication {
    public void start() {
        System.out.println("Welcome to Monsters and Heroes!");
        ConsoleUI ui = new ConsoleUI(new Scanner(System.in));  //把怎麼做放入consoleui 這樣就可以讓這個class簡短的
        try {
            Path dataDirectory = Path.of(System.getProperty("game.data", "data"));//把路徑字串轉成Path物件,可以用它代表那個資料夾去讀檔
            DataLoader loader = new DataLoader(dataDirectory);

            List<Hero> choices = loader.loadHeroes();
            List<Item> catalog = loader.loadItems();
            MonsterFactory factory = loader.loadMonsterFactory();

            Random random = System.getProperty("game.seed") == null ? new Random()  //準備遊戲要用的隨機來源
                : new Random(Long.parseLong(System.getProperty("game.seed")));//如果有設定 -Dgame.seed 就把那個文字用 Long.parseLong 轉成數字當種子,這樣每次隨機結果可重現;沒設定就用 new Random() 每次都不同

            int count = ui.number("Party size (1-3, Q quits): ", 1, GameRules.MAX_PARTY);  //選隊伍
            Party party = new Party();
            for (int i = 0; i < choices.size(); i++) { //把每個英雄選項印出來
                System.out.println((i + 1) + ". " + choices.get(i).describe());
            }
            while (party.getHeroes().size() < count) {  //保證不會選到重複的角色
                int index = ui.number("Choose hero " + (party.getHeroes().size() + 1) + ": ", 1, choices.size()) - 1;
                Hero hero = choices.get(index);
                if (party.getHeroes().contains(hero)) {
                    System.out.println("Already selected. Choose another hero.");
                } else { party.add(hero); }
            }
            World world = new World(GameRules.WORLD_SIZE, catalog, random); //建立地圖物件 整個世界
            ui.setMapDisplay(world::display);  //交給ui
            ui.setInformationDisplay(party::display);
            new AdventureGame(party, world, factory, ui, random).play();
        } catch (QuitGameException e) {
            System.out.println("Thanks for playing!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Cannot start game: " + e.getMessage());
            System.out.println("Run from the project folder containing data/, or set -Dgame.data=PATH."); //指定資料資料夾
        }
    }
}
