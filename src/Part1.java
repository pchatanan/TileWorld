import Tile.State;
import Tile.Tile;

public class Part1 {

    private static final int WIDTH = 6;
    private static final int HEIGHT = 6;
    private static final String WALL_COORS = "(1,0)(4,1)(1,4)(2,4)(3,4)";
    private static final String GREEN_COORS = "(0,0)(2,0)(5,0)(3,1)(4,2)(5,3)";
    private static final String ORANGE_COORS = "(1,1)(2,2)(3,3)(4,4)(5,1)";
    private static final double DISCOUNT = 0.99;
    private static final int ITERATION = 77;


    public static void main(String [] args)
    {
        // Part 1: perform Value Iteration
        TileWorld tileWorld1 = new TileWorld(WIDTH, HEIGHT, WALL_COORS, GREEN_COORS, ORANGE_COORS);
        Action[][] policy1 = PolicyMaker.performValueIteration(tileWorld1, DISCOUNT, ITERATION);
        //...
        // print outputs
        for(int x = 0; x < tileWorld1.getWidth(); x++) {
            for(int y = 0; y < tileWorld1.getHeight(); y++) {
                Tile tile = tileWorld1.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    State state = (State) tile;
                    double utility = state.getUtility();
                    String tileId = TileWorldUtil.encodeCoordinate(x, y);
                    System.out.println(String.format("%-12s %-12s %.3f", tileId, policy1[x][y], utility));
                }
            }
        }

        // Part 2: perform Policy Iteration
        TileWorld tileWorld2 = new TileWorld(WIDTH, HEIGHT, WALL_COORS, GREEN_COORS, ORANGE_COORS);
        Action[][] policy2 = PolicyMaker.performPolicyIteration(tileWorld2, DISCOUNT);
        for(int x = 0; x < tileWorld2.getWidth(); x++) {
            for(int y = 0; y < tileWorld2.getHeight(); y++) {
                Tile tile = tileWorld2.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    String tileId = TileWorldUtil.encodeCoordinate(x, y);
                    State state = (State) tile;
                    double utility = state.getUtility();
                    System.out.println(String.format("%-12s %-12s %.3f", tileId, policy2[x][y], utility));
                }
            }
        }

        // check if the 2 optimal policies are identical
        for(int x = 0; x < policy1.length; x++) {
            for (int y = 0; y < policy1.length; y++) {
                Tile tile = tileWorld2.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    if(policy1[x][y].getIndex() != policy2[x][y].getIndex()) {
                        try {
                            throw new Exception("Optimal policies are not identical.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }



    }
}
