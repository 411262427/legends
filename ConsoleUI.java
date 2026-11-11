import java.util.Scanner;

//統一處理輸入，所有選單共用同一個Scanner
public class ConsoleUI {
    private final Scanner scanner; //保存外面傳進來的Scanner，不另外建立
    private Runnable mapDisplay; //保存顯示地圖的動作，Runnable是Java內建介面
    private Runnable informationDisplay; //保存顯示資訊的動作，還沒設定時是null

    public void setMapDisplay(Runnable display) { mapDisplay = display; } //設定顯示地圖時要做的事
    public void setInformationDisplay(Runnable display) { informationDisplay = display; } //設定顯示資訊時要做的事
    public ConsoleUI(Scanner scanner) { this.scanner = scanner; } //建構子，把收到的Scanner參考存到欄位

    public String read(String prompt) { //顯示提示並讀取文字，先處理共用指令
        while (true) {
            System.out.print(prompt); //印出傳進來的提示文字，不換行
            if (!scanner.hasNextLine()) { throw new QuitGameException(); } //輸入已結束就提出退出要求
            String input = scanner.nextLine().trim(); //讀取一整行，去掉前後空白
            if (input.equalsIgnoreCase("q")) { throw new QuitGameException(); } //不分大小寫，輸入Q就退出
            if (input.equalsIgnoreCase("p") && mapDisplay != null) {
                mapDisplay.run(); continue; //執行已設定的地圖顯示動作，再回到迴圈等待輸入
            }
            if (input.equalsIgnoreCase("i") && informationDisplay != null) {
                informationDisplay.run(); continue; //顯示資訊後重新等待輸入，不把I傳回外面
            }
            if (input.equalsIgnoreCase("h")) { help(); continue; } //顯示說明後重新等待輸入
            return input; //其他輸入交回呼叫read的方法處理
        }
    }

    public int number(String prompt, int min, int max) { //讀取指定範圍內的整數
        while (true) {
            String input = read(prompt); //沿用read，所以選數字時也能輸入Q、P、I、H
            try {
                int value = Integer.parseInt(input); //把輸入的字串轉成int
                if (value >= min && value <= max) { return value; } //範圍正確才回傳，包含上下限
            } catch (NumberFormatException ignored) {
                //不是整數或數字超過int範圍，就接著顯示提示並重新輸入
            }
            System.out.println("Enter a number from " + min + " to " + max + ". Q quits."); //格式錯誤或超出範圍都會印出
        }
    }

    public void help() { //只印出操作說明，實際規則由其他類別執行
        System.out.println("\nW/A/S/D: move the entire party | M: market | B: inventory");
        System.out.println("I: party information | P: map | H: help | Q: quit anywhere");
        System.out.println("Start on a market tile: buy and equip items before exploring.");
        System.out.println("Battle: attack, cast, drink, equip; information does not use a turn.");
        System.out.println("Each living hero acts once, then living monsters attack. All heroes faint = game over.");
        System.out.println("Each hero has a separate wallet and inventory. 0 cancels numbered selections.");
        System.out.println("Weapons/armor wear out; spells have 3 uses; potions have 1 use.");
    }
}