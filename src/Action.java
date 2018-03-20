import java.util.Random;

public enum Action {
    ACTION_UP(0, 0, -1),
    ACTION_RIGHT(1, 1, 0),
    ACTION_DOWN(2, 0, 1),
    ACTION_LEFT(3, -1, 0);

    private int index;
    private int xUpdate;
    private int yUpdate;

    Action(int index, int xUpdate, int yUpdate) {
        this.index = index;
        this.xUpdate = xUpdate;
        this.yUpdate = yUpdate;
    }

    public int getIndex() {
        return index;
    }

    public static Action getAction(int index) {
        for (Action action : values()) {
            if (index == action.getIndex()) {
                return action;
            }
        }
        return null;
    }

    public int getXUpdate() {
        return xUpdate;
    }

    public int getYUpdate() {
        return yUpdate;
    }

    public Action rightAction() {
        int rightIndex = (index + 1) % 4;
        return getAction(rightIndex);
    }

    public Action leftAction() {
        int leftIndex = (index - 1 < 0) ? values().length - 1 : index - 1;
        return getAction(leftIndex);
    }

    private static final Random RANDOM = new Random();

    public static Action randomAction() {
        return getAction(RANDOM.nextInt(values().length));
    }

    private double expectedUtility;

    public double getExpectedUtility() {
        return expectedUtility;
    }

    public void setExpectedUtility(double expectedUtility) {
        this.expectedUtility = expectedUtility;
    }

}
