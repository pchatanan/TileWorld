import Jama.Matrix;
import Tile.State;
import Tile.Tile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolicyMaker {

    public static Action[][] performPolicyIteration(TileWorld tileWorld, double discount) {
        Action[][] policy = new Action[tileWorld.getWidth()][tileWorld.getHeight()];
        // initialize policy to random actions
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                policy[x][y] = Action.randomAction();
            }
        }
        boolean unchanged;
        int round = 0;
        while (true) {
            round++;
            unchanged = true;
            // perform policy evaluation
            performPolicyEvaluation(tileWorld, policy, discount);
            // perform policy improvement
            for(int x = 0; x < tileWorld.getWidth(); x++) {
                for(int y = 0; y < tileWorld.getHeight(); y++) {
                    Tile tile = tileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        State oldState = (State) tile;
                        Action oldAction = policy[x][y];
                        Action newAction = getBestAction(tileWorld, oldState);
                        if(oldAction.getIndex() != newAction.getIndex()) {
                            policy[x][y] = newAction;
                            unchanged = false;
                        }
                    }
                }
            }
            if(unchanged) {
                break;
            }
            saveValue(tileWorld, round, "policyIteration");
        }
        return policy;
    }

    public static List<Action[][]> performPolicyIterationBonus(TileWorld tileWorld, double discount) {
        List<Action[][]> policyList = new ArrayList<>();
        // initialize policy to random actions
        Action[][] policy = new Action[tileWorld.getWidth()][tileWorld.getHeight()];
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                policy[x][y] = Action.randomAction();
            }
        }
        policyList.add(policy);
        boolean unchanged;
        int round = 0;
        while (true) {
            round++;
            unchanged = true;
            Action[][] nextPolicy = policyList.get(policyList.size() - 1);
            // perform policy evaluation
            performPolicyEvaluation(tileWorld, nextPolicy, discount);
            // perform policy improvement
            for(int x = 0; x < tileWorld.getWidth(); x++) {
                for(int y = 0; y < tileWorld.getHeight(); y++) {
                    Tile tile = tileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        State oldState = (State) tile;
                        Action oldAction = nextPolicy[x][y];
                        Action newAction = getBestAction(tileWorld, oldState);
                        double oldUtil = getExpectedUtility(tileWorld, oldState, oldAction);
                        double newUtil = getExpectedUtility(tileWorld, oldState, newAction);
                        if(oldAction.getIndex() != newAction.getIndex() && newUtil > oldUtil) {
                            nextPolicy[x][y] = newAction;
                            unchanged = false;
                        }
                    }
                }
            }
            policyList.add(nextPolicy);
            if(unchanged) {
                break;
            }
        }
        return policyList;
    }

    private static void performPolicyEvaluation(TileWorld tileWorld, Action[][] policy, double discount) {
        double[][] lhsArray = new double[tileWorld.getNumState()][tileWorld.getNumState()];
        double[] rhsArray = new double[tileWorld.getNumState()];
        // calculate utility
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                Tile tile = tileWorld.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    State state = (State) tile;
                    Action action = policy[x][y];
                    lhsArray[state.getIndex()] = getLinearEquation(tileWorld, state, action, discount);
                    rhsArray[state.getIndex()] = -1*state.reward();
                }
            }
        }
        //Creating Matrix Objects with arrays
        Matrix lhs = new Matrix(lhsArray);
        Matrix rhs = new Matrix(rhsArray, tileWorld.getNumState());
        //Calculate Solved Matrix
        Matrix ans = lhs.solve(rhs);
        // update utility
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                Tile tile = tileWorld.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    State state = (State) tile;
                    if(state.getUtility() < ans.get(state.getIndex(), 0)){
                        state.setNewUtility(ans.get(state.getIndex(), 0));
                        state.commitNewUtility();
                    }
                }
            }
        }
    }

    public static  Action[][] performValueIteration(TileWorld tileWorld, double discount, int iteration) {
        Action[][] policy = new Action[tileWorld.getWidth()][tileWorld.getHeight()];
        for(int i = 0; i < iteration; i++) {
            // calculate utility
            for(int x = 0; x < tileWorld.getWidth(); x++) {
                for(int y = 0; y < tileWorld.getHeight(); y++) {
                    Tile tile = tileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        State state = (State) tile;
                        Action bestAction = getBestAction(tileWorld, state);
                        // update utility
                        double newUtility = state.reward()
                                + (discount*getExpectedUtility(tileWorld, state, bestAction));
                        state.setNewUtility(newUtility);
                    }
                }
            }
            // update utility
            for(int x = 0; x < tileWorld.getWidth(); x++) {
                for(int y = 0; y < tileWorld.getHeight(); y++) {
                    Tile tile = tileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        State state = (State) tile;
                        state.commitNewUtility();
                    }
                }
            }
            saveValue(tileWorld, i, "myfile");
        }
        // final policy
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                Tile tile = tileWorld.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    State state = (State) tile;
                    policy[x][y] = getBestAction(tileWorld, state);
                }
            }
        }
        return policy;
    }

    private static void saveValue(TileWorld tileWorld, int iteration, String title) {
        StringBuilder stringBuilder = new StringBuilder();
        String prefix = "";
        if(iteration == 0) {
            for(int x = 0; x < tileWorld.getWidth(); x++) {
                for(int y = 0; y < tileWorld.getHeight(); y++) {
                    Tile tile = tileWorld.getTiles()[x][y];
                    if(tile.getTileType() == Tile.TileType.STATE) {
                        String stateId = TileWorldUtil.encodeCoordinate(x, y);
                        stringBuilder.append(prefix);
                        prefix = " ";
                        stringBuilder.append(stateId);
                    }
                }
            }
            stringBuilder.append("\r\n");
        }
        prefix = "";
        for(int x = 0; x < tileWorld.getWidth(); x++) {
            for(int y = 0; y < tileWorld.getHeight(); y++) {
                Tile tile = tileWorld.getTiles()[x][y];
                if(tile.getTileType() == Tile.TileType.STATE) {
                    State state = (State) tile;
                    stringBuilder.append(prefix);
                    prefix = " ";
                    stringBuilder.append(String.valueOf(state.getUtility()));
                }
            }
        }
        stringBuilder.append("\n");
        try {
            final Path path = Paths.get(title + ".txt");
            Files.write(Paths.get(title + ".txt"), Collections.singletonList(stringBuilder.toString()), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
            System.out.println(e);
        }
    }

    private static Action getBestAction(TileWorld tileWorld, State oldState) {
        double maxExpectedUtility = -Double.MAX_VALUE;
        Action bestAction = null;
        for(Action intendedAction: Action.values()) {
            double expectedUtility = getExpectedUtility(tileWorld, oldState, intendedAction);
            if(expectedUtility > maxExpectedUtility) {
                maxExpectedUtility = expectedUtility;
                bestAction = intendedAction;
            }
        }
        return bestAction;
    }

    private static double getExpectedUtility(TileWorld tileWorld, State oldState, Action intendedAction) {
        double expectedUtility = 0;
        State intendedState = tileWorld.getNewState(oldState, intendedAction);
        State rightState = tileWorld.getNewState(oldState, intendedAction.rightAction());
        State leftState = tileWorld.getNewState(oldState, intendedAction.leftAction());

        expectedUtility += (tileWorld.transitionModel(intendedState, oldState, intendedAction)
                *intendedState.getUtility());
        if(!rightState.equals(intendedState)) {
            expectedUtility += (tileWorld.transitionModel(rightState, oldState, intendedAction)
                    *rightState.getUtility());
        }
        if(!leftState.equals(intendedState) && !leftState.equals(rightState)) {
            expectedUtility += (tileWorld.transitionModel(leftState, oldState, intendedAction)
                    *leftState.getUtility());
        }
        return expectedUtility;
    }

    private static double[] getLinearEquation(TileWorld tileWorld, State state, Action action, double discount) {
        double[] equation = new double[tileWorld.getNumState()];

        State intendedState = tileWorld.getNewState(state, action);
        State rightState = tileWorld.getNewState(state, action.rightAction());
        State leftState = tileWorld.getNewState(state, action.leftAction());

        equation[intendedState.getIndex()] = discount*tileWorld.transitionModel(intendedState, state, action);
        if(!rightState.equals(intendedState)) {
            equation[rightState.getIndex()] = discount*(tileWorld.transitionModel(rightState, state, action));
        }
        if(!leftState.equals(intendedState) && !leftState.equals(rightState)) {
            equation[leftState.getIndex()] = discount*(tileWorld.transitionModel(leftState, state, action));
        }
        equation[state.getIndex()] -= 1;
        return equation;
    }




}
