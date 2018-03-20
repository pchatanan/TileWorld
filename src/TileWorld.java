import Tile.Tile;
import Tile.Wall;
import Tile.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileWorld {

    private Tile[][] tiles;
    private int width;
    private int height;
    private int numState;

    private String walls;
    private String green_states;
    private String orange_states;

    // constructor
    public TileWorld(int width, int height, String walls, String green_states, String orange_states) {

        this.width = width;
        this.height = height;
        this.walls = walls;
        this.green_states = green_states;
        this.orange_states = orange_states;

        tiles = new Tile[width][height];

        int index = 0;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                String encodedCoor = TileWorldUtil.encodeCoordinate(x, y);
                if(walls.contains(encodedCoor)) {
                    // add wall
                    tiles[x][y] = new Wall(x, y);
                }
                else if(green_states.contains(encodedCoor)) {
                    // add green state
                    tiles[x][y] = new State(x, y, State.StateType.STATE_GREEN, index);
                    index++;
                }
                else if(orange_states.contains(encodedCoor)) {
                    // add orange state
                    tiles[x][y] = new State(x, y, State.StateType.STATE_ORANGE, index);
                    index++;
                }
                else {
                    // add blank state
                    tiles[x][y] = new State(x, y, State.StateType.STATE_BLANK, index);
                    index++;
                }
            }
        }
        this.numState = index;
    }

    public double transitionModel(State newState, State oldState, Action intendedAction) {
        // return probability of going to newState given oldState and intendedAction taken
        double probability = 0;
        State intendedState = getNewState(oldState, intendedAction);
        State rightState = getNewState(oldState, intendedAction.rightAction());
        State leftState = getNewState(oldState, intendedAction.leftAction());

        if(newState.equals(intendedState)) {
            probability += 0.8;
        }
        if(newState.equals(rightState)) {
            probability += 0.1;
        }
        if(newState.equals(leftState)) {
            probability += 0.1;
        }
        return probability;
    }

    public State getNewState(State oldState, Action action) {
        int newX = oldState.getX() + action.getXUpdate();
        int newY = oldState.getY() + action.getYUpdate();

        boolean inBoundsX = (newX >= 0) && (newX < width);
        boolean inBoundsY = (newY >= 0) && (newY < height);

        if(inBoundsX && inBoundsY) {
            // in bound
            Tile newTile = tiles[newX][newY];
            if(newTile.getTileType() == Tile.TileType.WALL) {
                // move into wall
                return oldState;
            }
            else {
                return (State) newTile;
            }
        }
        else {
            // out bound
            return oldState;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getNumState() {
        return numState;
    }

    public String getMaze() {
        return walls + "\n" + green_states + "\n" + orange_states;
    }

    public static TileWorld getRandomTileWorld(List<Integer> tileTypeList, int width, int height) {
        // generate a random list defining type of 36 tiles
        List<Integer> tileList = new ArrayList<>();
        for(int a = 0; a < tileTypeList.size(); a++) {
            for(int b = 0; b < tileTypeList.get(a); b++) {
                tileList.add(a);
            }
        }
        Collections.shuffle(tileList);

        // generate string descriptor of tile world
        String WALL_COORS = "";
        String GREEN_COORS = "";
        String ORANGE_COORS = "";
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                String tileId = TileWorldUtil.encodeCoordinate(x, y);
                int randomNum =  tileList.get(0);
                tileList.remove(0);
                switch (randomNum) {
                    case 0:
                        WALL_COORS += tileId;
                        break;
                    case 1:
                        GREEN_COORS += tileId;
                        break;
                    case 2:
                        ORANGE_COORS += tileId;
                        break;
                }
            }
        }
        // create tile world
        TileWorld tileWorld = new TileWorld(width, height, WALL_COORS, GREEN_COORS, ORANGE_COORS);
        return tileWorld;
    }
}
