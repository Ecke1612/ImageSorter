package objects;

import javafx.scene.paint.Color;

public class TagObject {

    private int index;
    private String name;
    private Color color;

    public TagObject(int index, String name, Color color) {
        this.index = index;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }
}
