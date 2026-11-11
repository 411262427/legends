import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
//Generates a connected map, manages party movement, and displays tiles and the party's position.
public class World {
    private final Tile[][] tiles;
    private int row;
    private int col;

    public World(int size, List<Item> catalog, Random random) {
        if (size < 2 || catalog.isEmpty()) { throw new IllegalArgumentException("Invalid world."); }
        tiles = new Tile[size][size];
        List<Item> order = new ArrayList<>(catalog);
        Collections.shuffle(order, random);
        int marketIndex = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                double roll = random.nextDouble();
                if ((r == 0 && c == 0) || roll < 0.30) {
                    List<Item> stock = new ArrayList<>();
                    //每個商店使用不同的商品輪替組合；起點商店包含所有一級商品
                    for (int i = 0; i < order.size(); i++) {
                        if ((r == 0 && c == 0 && order.get(i).getRequiredLevel() == 1)
                            || (i + marketIndex) % 3 == 0) { stock.add(order.get(i)); }
                    }
                    //加入該商店獨有的平價藥水，確保各商店庫存不同
                    stock.add(new Potion("Market_" + marketIndex + "_Tonic", 50, 1, 25,
                        java.util.EnumSet.of(Stat.HEALTH)));
                    tiles[r][c] = new Tile(Tile.Type.MARKET, new Market(stock));
                    marketIndex++;
                } else {
                    tiles[r][c] = new Tile(roll < 0.50 ? Tile.Type.BLOCKED : Tile.Type.COMMON, null);
                }
            }
        }
        connectMap();
    }
    //以連通區搜尋移除必要的障礙，確保所有可走格子都能到達
    private void connectMap() {
        while (true) {
            boolean[][] reached = reachable();
            int missingR = -1, missingC = -1;
            for (int r = 0; r < tiles.length && missingR < 0; r++) {
                for (int c = 0; c < tiles.length; c++) {
                    if (tiles[r][c].isAccessible() && !reached[r][c]) {
                        missingR = r; missingC = c; break;
                    }
                }
            }
            if (missingR < 0) { return; }
            for (int r = 0; r <= missingR; r++) {
                if (!tiles[r][0].isAccessible()) { tiles[r][0] = new Tile(Tile.Type.COMMON, null); }
            }
            for (int c = 0; c <= missingC; c++) {
                if (!tiles[missingR][c].isAccessible()) { tiles[missingR][c] = new Tile(Tile.Type.COMMON, null); }
            }
        }
    }
    private boolean[][] reachable() {
        boolean[][] reached = new boolean[tiles.length][tiles.length];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0}); reached[0][0] = true;
        int[][] steps = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int[] pos = queue.remove();
            for (int[] step : steps) {
                int r = pos[0] + step[0], c = pos[1] + step[1];
                if (inBounds(r,c) && !reached[r][c] && tiles[r][c].isAccessible()) {
                    reached[r][c] = true; queue.add(new int[]{r,c});
                }
            }
        }
        return reached;
    }
    private boolean inBounds(int r, int c) { return r >= 0 && c >= 0 && r < tiles.length && c < tiles.length; }
    public Tile currentTile() { return tiles[row][col]; }
    public boolean move(String direction) {
        int r = row, c = col;
        switch (direction.toLowerCase()) {
            case "w": r--; break;
            case "s": r++; break;
            case "a": c--; break;
            case "d": c++; break;
            default: return false;
        }
        if (!inBounds(r,c) || !tiles[r][c].isAccessible()) { return false; }
        row = r; col = c;
        return true;
    }
    public void display() {
        System.out.println("\nMap: @ party, M market, . common, # blocked");
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles.length; c++) {
                System.out.print((r == row && c == col ? "@" : tiles[r][c].symbol()) + " ");
            }
            System.out.println();
        }
        System.out.println("Position: (" + row + ", " + col + ") " + currentTile().getType());
    }
}
