package Tile;

public class Tile {

    public enum TileType {
        STATE,
        WALL
    }

    protected int x;
    protected int y;
    private TileType tileType;

    public Tile(int x, int y, TileType tileType) {
        this.x = x;
        this.y = y;
        this.tileType = tileType;
    }

    public TileType getTileType() {
        return tileType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
