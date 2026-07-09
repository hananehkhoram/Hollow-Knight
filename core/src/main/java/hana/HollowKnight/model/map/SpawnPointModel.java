package hana.HollowKnight.model.map;

public class SpawnPointModel {
    private final String name;
    private final float x, y;

    public SpawnPointModel(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public float getX() { return x; }
    public float getY() { return y; }
}
