package Tile;

public class State extends Tile {

    public enum StateType {
        STATE_BLANK(-0.04),
        STATE_GREEN(1),
        STATE_ORANGE(-1);

        private double reward;

        StateType(double reward) {
            this.reward = reward;
        }

        public double getReward() {
            return reward;
        }
    }

    private StateType stateType;
    private double utility;
    private double newUtility;
    private int index;

    public State(int x, int y, StateType stateType, int index) {
        super(x, y, Tile.TileType.STATE);
        this.stateType = stateType;
        this.utility = 0;
        this.newUtility = 0;
        this.index = index;
    }

    public StateType getStateType() {
        return stateType;
    }

    public double getUtility() {
        return utility;
    }

    public void setNewUtility(double newUtility) {
        this.newUtility = newUtility;
    }

    public void commitNewUtility() {
        this.utility = this.newUtility;
    }
    // ...

    public double reward() {
        return stateType.getReward();
    }

    public boolean equals(State state) {
        return super.x == state.x && super.y == state.y;
    }

    public int getIndex() {
        return index;
    }
}
