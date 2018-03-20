import Tile.Tile;
import Tile.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Part2 {

    private static final int WIDTH = 6;
    private static final int HEIGHT = 6;
    private static final int N_TILE_TYPE = 4;
    private static final int LOOP = 10000;
    private static final double DISCOUNT = 0.99;

    public static void main(String [] args) {

        int maxIteration = 0;
        List<Action[][]> complexPolicyList = null;
        TileWorld mostComplexTileWorld = null;

        for (int i = 0; i < LOOP; i++) {
            List<Integer> tileTypeList = n_random(WIDTH*HEIGHT, N_TILE_TYPE);
            TileWorld tileWorld = TileWorld.getRandomTileWorld(tileTypeList, WIDTH, HEIGHT);
            List<Action[][]> policyList = PolicyMaker.performPolicyIterationBonus(tileWorld, DISCOUNT);
            if(policyList.size() > maxIteration) {
                maxIteration = policyList.size();
                complexPolicyList = policyList;
                mostComplexTileWorld = tileWorld;
            }
        }

        if(mostComplexTileWorld != null) {
            System.out.println(maxIteration);
            System.out.println(mostComplexTileWorld.getMaze());
            for(int x = 0; x < mostComplexTileWorld.getWidth(); x++) {
                for(int y = 0; y < mostComplexTileWorld.getHeight(); y++) {
                    Tile tile = mostComplexTileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        String tileId = TileWorldUtil.encodeCoordinate(x, y);
                        State state = (State) tile;
                        double utility = state.getUtility();
                        System.out.println(String.format("%-12s %-12s %.3f", tileId, complexPolicyList.get(complexPolicyList.size() - 1)[x][y], utility));
                    }
                }
            }
        }

    }

    private static List<Integer> n_random(int targetSum, int numberOfDraws) {
        Random r = new Random();
        List<Integer> load = new ArrayList<>();

        //random numbers
        int sum = 0;
        for (int i = 0; i < numberOfDraws; i++) {
            int next = r.nextInt(targetSum) + 1;
            load.add(next);
            sum += next;
        }

        //scale to the desired target sum
        double scale = 1d * targetSum / sum;
        sum = 0;
        for (int i = 0; i < numberOfDraws; i++) {
            load.set(i, (int) (load.get(i) * scale));
            sum += load.get(i);
        }

        //take rounding issues into account
        while(sum++ < targetSum) {
            int i = r.nextInt(numberOfDraws);
            load.set(i, load.get(i) + 1);
        }

        return load;
    }
}
