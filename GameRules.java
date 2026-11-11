//Stores shared game constants and provides a safe rounding method.用來集中保存遊戲設定
public final class GameRules {
    public static final int WORLD_SIZE = 8;
    public static final int MAX_PARTY = 3;
    public static final double ENCOUNTER_CHANCE = 0.25; //普通格子的遇怪機率為 25%
    public static final double ATTACK_SCALE = 0.05;
    public static final double DEFENSE_SCALE = 0.05;
    public static final double MAX_DODGE = 0.60; //英雄閃避率上限為 60%
    private GameRules() { } //把建構子設為 private，避免外面建立 GameRules 物件

    public static int rounded(double value) { //把數字限制為不小於 0、向上取整，且最多 1,000,000，再轉成 int
        return (int) Math.min(1_000_000, Math.ceil(Math.max(0, value)));
    }
}
