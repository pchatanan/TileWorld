import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Part3 {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int N_TILE_TYPE = 4;
    private static final int LOOP = 500;
    private static final double DISCOUNT = 0.99;

    public static void main(String [] args) {

        for(int n_states = 1; n_states < WIDTH*HEIGHT + 1; n_states++) {
            int sum = 0;
            for(int i = 0; i < LOOP; i++) {
                List<Integer> tileTypeList = new ArrayList<>();
                // add walls number
                tileTypeList.add(WIDTH*HEIGHT - n_states);
                // add other states
                tileTypeList.addAll(n_random(n_states, N_TILE_TYPE - 1));
                TileWorld tileWorld = TileWorld.getRandomTileWorld(tileTypeList, WIDTH, HEIGHT);
                List<Action[][]> policyList = PolicyMaker.performPolicyIterationBonus(tileWorld, DISCOUNT);
                sum += policyList.size();
            }
            double average = sum / (double) LOOP;
            System.out.println(String.valueOf(n_states) + "," + average);
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
